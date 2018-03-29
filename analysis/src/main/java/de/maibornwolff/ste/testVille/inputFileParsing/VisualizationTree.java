package de.maibornwolff.ste.testVille.inputFileParsing;

import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.inputFileParsing.common.IDGenerator;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VisualizationTree extends ComposedItem {

    private static  IDGenerator localIdGenerator = new IDGenerator();
    private static  String      nodeName         = "groupedItem";

    private static int generateNextLocalKey() {
        return localIdGenerator.generateNextUniqueKey();
    }

    private VisualizationTree() {
        super(generateNextLocalKey());
        this.setName("root");
        this.setPriority("root");
    }

    public static VisualizationTree buildVisualizationTreeFrom(Collection<Item> items) {
        VisualizationTree tree = new VisualizationTree();
        Set<Item> itemsAfterInnerGrouping = groupInnerItemsOf(items);
        tree.setAssociatedItems(buildItemGroups(itemsAfterInnerGrouping));
        System.out.println(tree.getAssociatedItems().size());
        return tree;
    }

    private static Set<Item> groupInnerItemsOf(Collection<Item> items) {
        items.forEach(x -> {
            if(isComposedItem(x)) groupInnerItemsOf((ComposedItem) x);
        });

        return new HashSet<>(items);
    }

    private static boolean isComposedItem(Item i) {
        return (i instanceof ComposedItem);
    }

    private static void groupInnerItemsOf(ComposedItem ci) {
        ci.setAssociatedItems(buildItemGroups(ci.getAssociatedItems()));
    }

    private static Set<Item> buildItemGroups(Collection<Item> allItems) {
        Set<String> availablePriorities = extractAssociatedItemPriorities(allItems);
        //System.out.println(allItems);
        return availablePriorities.stream().map(x -> buildItemGroup(x, allItems)).collect(Collectors.toSet());
    }

    private static ComposedItem buildItemGroup(String groupPriority, Collection<Item> allItems) {
        ComposedItem result = new ComposedItem(generateNextLocalKey());
        result.addAllAssociatedItems(extractAssociatedItemWithPriority(groupPriority, allItems));
        result.setName(nodeName);
        result.setPriority(groupPriority);
        return result;
    }

    private static Set<String> extractAssociatedItemPriorities(Collection<Item> allItems) {
        return allItems.stream().map(Item:: getPriority).collect(Collectors.toSet());
    }

    private static List<Item> extractAssociatedItemWithPriority(String priority, Collection<Item> allItems) {
        return allItems
                .stream()
                .filter(x -> x.getPriority().equals(priority))
                .collect(Collectors.toList());
    }

    /*private static int maxDepthOfComposedItem(Item ci) {
        if(! isComposedItem(ci)) return -1;
        ComposedItem ci1 = (ComposedItem)ci;
        if (ci1.getAssociatedItems().isEmpty()) return 0;

        int maxDepth = 0;
        for (Item i: ci1.getAssociatedItems()) {
            int childMaxDepth = maxDepthOfComposedItem(i);
            if(maxDepth < childMaxDepth) {
                maxDepth = childMaxDepth;
            }
        }
        return 1 + maxDepth;
    }*/
}