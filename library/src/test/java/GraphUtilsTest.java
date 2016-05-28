import com.google.common.collect.Lists;
import cs.technion.ac.il.sd.library.graph.GraphTraverse;
import cs.technion.ac.il.sd.library.graph.GraphUtils;
import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Test file for {@link GraphUtils#toposort(DirectedGraph)}
 */
public class GraphUtilsTest {

    private static final boolean PRINT_ON_FAIL = true;

    private static final DirectedAcyclicGraph<Integer, DefaultEdge> smallGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    private static final DirectedAcyclicGraph<Integer, DefaultEdge> emptyGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    private static final DirectedAcyclicGraph<Integer, DefaultEdge> complexGraph = new DirectedAcyclicGraph<>(DefaultEdge.class);
    private static final DirectedGraph<Integer, DefaultEdge> cyclicGraph = new DefaultDirectedGraph<>(DefaultEdge.class);

    private static final DirectedGraph<Integer, DefaultEdge> binaryTree = new DefaultDirectedGraph<>(DefaultEdge.class);

    private <V, E> Optional<Iterator<V>> toposort(DirectedGraph<V, E> graph) {
        return GraphUtils.toposort(graph);
    }

    private <V, E> boolean toposortInvariant(DirectedGraph<V, E> graph, Iterator<V> toposort) {

        Map<V, Integer> topoOrder = new HashMap<>();
        int order = 0;
        while (toposort.hasNext()) {
            topoOrder.put(toposort.next(), ++order);
        }
        if (topoOrder.keySet().size() != graph.vertexSet().size()) {
            return false;
        }

        for (E edge : graph.edgeSet()) {

            V source = graph.getEdgeSource(edge);
            V target = graph.getEdgeTarget(edge);

            if (topoOrder.get(source) > topoOrder.get(target)) {
                if (PRINT_ON_FAIL) {
                    System.out.println("toposort indexOf " + source + " = " + topoOrder.get(source));
                    System.out.println("toposort indexOf " + target + " = " + topoOrder.get(target));
                    System.out.println("graph = " + graph);
                    System.out.println("toposort = " + toposort);
                }
                return false;
            }
        }
        return true;
    }
    @SuppressWarnings("unchecked")

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @BeforeClass
    public static void setupSmall() {
        smallGraph.addVertex(1);
        smallGraph.addVertex(2);
        smallGraph.addVertex(3);
        smallGraph.addVertex(4);
        smallGraph.addEdge(1, 2);
        smallGraph.addEdge(1, 3);
        smallGraph.addEdge(3, 4);
        System.out.println("@setupSmall");
    }


    @BeforeClass
    public static void setupComplex() {
        complexGraph.addVertex(5);
        complexGraph.addVertex(7);
        complexGraph.addVertex(3);
        complexGraph.addVertex(11);
        complexGraph.addVertex(8);
        complexGraph.addVertex(2);
        complexGraph.addVertex(9);
        complexGraph.addVertex(10);
        complexGraph.addEdge(5, 11);
        complexGraph.addEdge(11, 2);
        complexGraph.addEdge(11, 9);
        complexGraph.addEdge(11, 10);
        complexGraph.addEdge(7, 11);
        complexGraph.addEdge(7, 8);
        complexGraph.addEdge(8, 9);
        complexGraph.addEdge(3, 8);
        complexGraph.addEdge(3, 10);
        System.out.println("@setupComplex");
    }

    @BeforeClass
    public static void setupCyclic() {
        cyclicGraph.addVertex(1);
        cyclicGraph.addVertex(2);
        cyclicGraph.addVertex(3);
        cyclicGraph.addVertex(4);
        cyclicGraph.addEdge(1, 2);
        cyclicGraph.addEdge(1, 3);
        cyclicGraph.addEdge(3, 4);
        cyclicGraph.addEdge(4, 1);
    }

    @BeforeClass
    public static void setupBinaryTree() {
        int root = -1;
        binaryTree.addVertex(root);
        for(int i=0; i<10; i++)
        {
            binaryTree.addVertex(i);
        }
        binaryTree.addEdge(root, 0);
        binaryTree.addEdge(root, 1);
        for(int i=2; i<10; i++)
        {
            binaryTree.addEdge(i-2, i);
        }


    }

    @Test
    public void nullGraphThrows() {
        thrown.expect(IllegalArgumentException.class);
        toposort(null);
    }
    @Test
    public void cyclicToposortFails() {
        Assert.assertEquals(true, GraphUtils.hasCycle(cyclicGraph));
        Assert.assertEquals(Optional.empty(), toposort(cyclicGraph));
    }

    @Rule
    public Timeout globalTimeout = Timeout.seconds(10);



    @Test
    public void smallGraphReturnsValidSort() {
        Optional<Iterator<Integer>> t = toposort(smallGraph);
        Assert.assertTrue(t.isPresent());
        List<Integer> topoSort = Lists.newArrayList(t.get());
        boolean sortValid = topoSort.equals(Arrays.asList(1, 2, 3, 4))
                || topoSort.equals(Arrays.asList(1, 3, 2, 4));

        Assert.assertTrue(toposortInvariant(smallGraph, topoSort.iterator()));
        Assert.assertTrue(sortValid);
    }


    @Test
    public void emptyGraphReturnsEmptySort() {
        Optional<Iterator<Integer>> topoSort = toposort(emptyGraph);
        topoSort.ifPresent(i -> Assert.assertFalse(i.hasNext()));
    }


    @Test
    public void complexGraphPreservesInvariant() {
        Optional<Iterator<Integer>> topoSort = toposort(complexGraph);
        Assert.assertTrue("should contain value", topoSort.isPresent());
        Assert.assertTrue(toposortInvariant(complexGraph, topoSort.get()));
    }
    @Test
    public void sourcesSmallGraphAreValid()
    {
        Set<Integer> expectedSources = new HashSet<>();
        expectedSources.add(1);
        Assert.assertEquals(GraphUtils.getSourcesVertices(smallGraph), expectedSources);
    }


    @Test
    public void sourcesComplexGraphAreValid()
    {
        Set<Integer> expectedSources = new HashSet<>();
        expectedSources.addAll(Arrays.asList(5,3,7));
        Assert.assertEquals(GraphUtils.getSourcesVertices(complexGraph), expectedSources);
    }

    @Test
    public void leavesSmallGraphAreValid()
    {
        Set<Integer> expectedLeaves = new HashSet<>();
        expectedLeaves.addAll(Arrays.asList(2,4));
        Assert.assertEquals(GraphUtils.getLeafVertices(smallGraph), expectedLeaves);
    }


    @Test
    public void leavesComplexGraphAreValid()
    {
        Set<Integer> expectedLeaves = new HashSet<>();
        expectedLeaves.addAll(Arrays.asList(2,9,10));
        Assert.assertEquals(GraphUtils.getLeafVertices(complexGraph), expectedLeaves);
    }

    /************ DFS ************/

    @Test
    public void dfsIterationFromRootOnBinaryTreeIsCorrect()
    {
        Iterator<Integer> t = GraphTraverse.DFSTraverseSingleComponent(binaryTree, Optional.of(-1));
        ArrayList<Integer> verteicesList = Lists.newArrayList(t);
        boolean dfsIterationCorrect = verteicesList.equals(Arrays.asList(-1,0,2,4,6,8,1, 3, 5, 7, 9))
                                        || verteicesList.equals(Arrays.asList(-1,1, 3, 5, 7, 9, 0,2,4,6,8));
        Assert.assertTrue(dfsIterationCorrect);
    }

    @Test
    public void singleComponentDfsIterationOnBinaryTreeIsCorrect()
    {
        ArrayList<Integer> singleComponentverteicesList = new ArrayList<>();
        GraphTraverse.DFSTraverseSingleComponent(binaryTree, Optional.of(2)).forEachRemaining(singleComponentverteicesList::add);
        Assert.assertEquals(singleComponentverteicesList,Arrays.asList(2,4,6,8));
        singleComponentverteicesList.clear();

        GraphTraverse.DFSTraverseSingleComponent(binaryTree, Optional.of(1)).forEachRemaining(singleComponentverteicesList::add);
        Assert.assertEquals(singleComponentverteicesList,Arrays.asList(1, 3, 5, 7, 9));
    }


    /**
     * NOTE: this test is implementation dependent and may false-fail if DepthSearchIterator implementation changes
     */
    @Test
    public void crossComponentDfsIterationOnBinaryTreeIsCorrect()
    {
        ArrayList<Integer> crossComponentverteicesList = new ArrayList<>();
        GraphTraverse.DFSTraverseCrossComponent(binaryTree, Optional.of(2)).forEachRemaining(crossComponentverteicesList::add);
        boolean crossComponentIterationCorrect = crossComponentverteicesList.equals(Arrays.asList(2,4,6,8,-1,0,1, 3, 5, 7, 9))
                || crossComponentverteicesList.equals(Arrays.asList(2, 4, 6, 8, -1, 1, 3, 5, 7, 9, 0)); //there are more valid options that are not tested here
        Assert.assertTrue(crossComponentIterationCorrect);
    }
    @Test
    public void dfsOnCyclicGraphTraveresEachVertexOnce()
    {
        ArrayList<Integer> verteicesList = new ArrayList<>();
        GraphTraverse.DFSTraverseSingleComponent(cyclicGraph, Optional.of(1)).forEachRemaining(verteicesList::add);
        boolean dfsIterationCorrect = verteicesList.equals(Arrays.asList(1,2,3,4))
                || verteicesList.equals(Arrays.asList(1,3, 4, 2));
        Assert.assertTrue(dfsIterationCorrect);
    }
    @Test
    public void dfsOnEmptyGraphReturnsEmptyIterator()
    {
        Assert.assertFalse(GraphTraverse.DFSTraverseSingleComponent(emptyGraph, Optional.empty()).hasNext());
        Assert.assertFalse(GraphTraverse.DFSTraverseCrossComponent(emptyGraph, Optional.empty()).hasNext());
    }
    @Test
    public void dfsOnAbsentStartVertexThrowsException()
    {
        thrown.expect(IllegalArgumentException.class);
        GraphTraverse.DFSTraverseSingleComponent(cyclicGraph, Optional.of(0));
    }
    @Test
    public void emptyStartVertexDfsOnArbitraryVertex()
    {
        ArrayList<Integer> verteicesList = new ArrayList<>();
        DirectedGraph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        for(int i = 0; i<3 ;i++){g.addVertex(i);}
        GraphTraverse.DFSTraverseSingleComponent(g, Optional.empty()).forEachRemaining(verteicesList::add);
        Assert.assertTrue(verteicesList.equals(Collections.singletonList(0)) || verteicesList.equals(Collections.singletonList(1)) || verteicesList.equals(Collections.singletonList(2)));

        verteicesList.clear();
        GraphTraverse.DFSTraverseCrossComponent(g, Optional.empty()).forEachRemaining(verteicesList::add);
        Assert.assertEquals(verteicesList.stream().collect(Collectors.toSet()), Arrays.asList(0,1,2).stream().collect(Collectors.toSet()));
    }

    /************ BFS ************/

    @Test
    public void bfsIterationFromRootOnBinaryTreeIsCorrect()
    {
        ArrayList<Integer> verteicesList = new ArrayList<>();
        GraphTraverse.BFSTraverseSingleComponent(binaryTree, Optional.of(-1)).forEachRemaining(verteicesList::add);
        boolean bfsIterationCorrect = verteicesList.equals(Arrays.asList(-1,0,1,2,3,4,5, 6, 7, 8, 9))
                || verteicesList.equals(Arrays.asList(-1,1,0,3,2,5,4, 7, 6, 9, 8));
        Assert.assertTrue(bfsIterationCorrect);
    }

    @Test
    public void singleComponentBfsIterationOnBinaryTreeIsCorrect()
    {
        ArrayList<Integer> singleComponentverteicesList = new ArrayList<>();
        GraphTraverse.BFSTraverseSingleComponent(binaryTree, Optional.of(2)).forEachRemaining(singleComponentverteicesList::add);
        Assert.assertEquals(singleComponentverteicesList,Arrays.asList(2,4,6,8));
        singleComponentverteicesList.clear();

        GraphTraverse.BFSTraverseSingleComponent(binaryTree, Optional.of(1)).forEachRemaining(singleComponentverteicesList::add);
        Assert.assertEquals(singleComponentverteicesList,Arrays.asList(1, 3, 5, 7, 9));
    }

    /**
     * NOTE: this test is implementation dependent and may false-fail if BreadthSearchIterator implementation changes
     */
    @Test
    public void crossComponentBfsIterationOnBinaryTreeIsCorrect()
    {
        ArrayList<Integer> crossComponentverteicesList = new ArrayList<>();
        GraphTraverse.BFSTraverseCrossComponent(binaryTree, Optional.of(2)).forEachRemaining(crossComponentverteicesList::add);
        boolean crossComponentIterationCorrect = crossComponentverteicesList.equals(Arrays.asList(2,4,6,8,-1,0,1, 3, 5, 7, 9)); //there are more valid options that are not tested here
        Assert.assertTrue(crossComponentIterationCorrect);
    }


    @Test
    public void bfsOnCyclicGraphTraveresEachVertexOnce()
    {
        ArrayList<Integer> verteicesList = new ArrayList<>();
        GraphTraverse.BFSTraverseSingleComponent(cyclicGraph, Optional.of(1)).forEachRemaining(verteicesList::add);
        boolean dfsIterationCorrect = verteicesList.equals(Arrays.asList(1,2,3,4))
                || verteicesList.equals(Arrays.asList(1,3, 2, 4));
        Assert.assertTrue(dfsIterationCorrect);
    }

    @Test
    public void bfsOnEmptyGraphReturnsEmptyIterator()
    {
        Assert.assertFalse(GraphTraverse.BFSTraverseSingleComponent(emptyGraph, Optional.empty()).hasNext());
        Assert.assertFalse(GraphTraverse.BFSTraverseCrossComponent(emptyGraph, Optional.empty()).hasNext());
    }

    @Test
    public void bfsOnAbsentStartVertexThrowsException()
    {
        thrown.expect(IllegalArgumentException.class);
        GraphTraverse.BFSTraverseSingleComponent(cyclicGraph, Optional.of(0));
    }

    @Test
    public void emptyStartVertexBfsOnArbitraryVertex()
    {
        ArrayList<Integer> verteicesList = new ArrayList<>();
        DirectedGraph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
        for(int i = 0; i<3 ;i++){g.addVertex(i);}

        GraphTraverse.BFSTraverseSingleComponent(g, Optional.empty()).forEachRemaining(verteicesList::add);
        Assert.assertTrue(verteicesList.equals(Collections.singletonList(0)) || verteicesList.equals(Collections.singletonList(1)) || verteicesList.equals(Collections.singletonList(2)));

        verteicesList.clear();
        GraphTraverse.BFSTraverseCrossComponent(g, Optional.empty()).forEachRemaining(verteicesList::add);
        Assert.assertEquals(verteicesList.stream().collect(Collectors.toSet()), new HashSet<>(Arrays.asList(0,1,2)));
    }
    @Test
    public void reachableVerticesComplexGraphCorrect()
    {
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,5),
                new HashSet<>(Arrays.asList(5,2,9,10,11)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,3),
                new HashSet<>(Arrays.asList(3,8,9,10)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,7),
                new HashSet<>(Arrays.asList(7,8,2,9,10,11)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,8),
                new HashSet<>(Arrays.asList(8,9)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,10),
                new HashSet<>(Collections.singletonList(10)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,11),
                new HashSet<>(Arrays.asList(2,9,10,11)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,2),
                new HashSet<>(Collections.singletonList(2)));
        Assert.assertEquals(GraphUtils.getAllReachableVerticesFromSource(complexGraph,9),
                new HashSet<>(Collections.singletonList(9)));
    }


}

