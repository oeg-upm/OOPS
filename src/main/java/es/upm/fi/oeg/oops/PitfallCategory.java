/*
 * SPDX-FileCopyrightText: 2025 Robin Vobruba <hoijui.quaero@gmail.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package es.upm.fi.oeg.oops;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

public class PitfallCategory {

    private static final Map<PitfallCategoryId, PitfallCategory> ID2CATEGORY;
    /**
     * This constitutes a tree from top to bottom.
     */
    private static Map<PitfallCategoryId, Set<TreeChild>> PARENT2CHILDREN = null;

    public static class TreeChild implements Comparable<TreeChild> {

        private final PitfallCategoryId category;
        private final PitfallId pitfall;

        public TreeChild(final PitfallCategoryId category) {
            this.category = category;
            this.pitfall = null;
        }

        public TreeChild(final PitfallId pitfall) {
            this.category = null;
            this.pitfall = pitfall;
        }

        @Override
        public int hashCode() {
            return Objects.hash(category, pitfall);
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof TreeChild)) {
                return false;
            }
            TreeChild other = (TreeChild) obj;
            return Objects.equals(category, other.category) && Objects.equals(pitfall, other.pitfall);
        }

        @Override
        public int compareTo(final TreeChild other) {
            if (this.isCategory() && other.isCategory()) {
                return this.getCategory().compareTo(other.getCategory());
            } else if (!this.isCategory() && !other.isCategory()) {
                return this.getPitfall().compareTo(other.getPitfall());
            } else if (this.isCategory()) {
                return 1;
            } else {
                return -1;
            }
        }

        public boolean isCategory() {
            return category != null;
        }

        public PitfallCategoryId getCategory() {
            return category;
        }

        public PitfallId getPitfall() {
            return pitfall;
        }

        @Override
        public String toString() {
            if (isCategory()) {
                return category.toString();
            } else {
                return pitfall.toString();
            }
        }
    }

    static {
        final Map<PitfallCategoryId, PitfallCategory> id2category = new HashMap<>();
        create(id2category, new PitfallCategoryId('H', 0), null, null);
        create(id2category, new PitfallCategoryId('C', 1), new PitfallCategoryId('H', 0),
                "Classification by Dimension");
        create(id2category, new PitfallCategoryId('S', 1), new PitfallCategoryId('C', 1), "Structural Dimension");
        create(id2category, new PitfallCategoryId('N', 1), new PitfallCategoryId('S', 1), "Modelling Decisions");
        create(id2category, new PitfallCategoryId('N', 2), new PitfallCategoryId('S', 1), "Wrong Inference");
        create(id2category, new PitfallCategoryId('N', 3), new PitfallCategoryId('S', 1), "No Inference");
        create(id2category, new PitfallCategoryId('N', 41), new PitfallCategoryId('S', 1), "Ontology language");
        create(id2category, new PitfallCategoryId('S', 2), new PitfallCategoryId('C', 1), "Functional Dimension");
        create(id2category, new PitfallCategoryId('N', 4), new PitfallCategoryId('S', 2),
                "Real World Modelling or Common Sense");
        create(id2category, new PitfallCategoryId('N', 5), new PitfallCategoryId('S', 2), "Requirements Completeness");
        create(id2category, new PitfallCategoryId('N', 51), new PitfallCategoryId('S', 2), "Application context");
        create(id2category, new PitfallCategoryId('S', 3), new PitfallCategoryId('C', 1),
                "Usability-Profiling Dimension");
        create(id2category, new PitfallCategoryId('N', 6), new PitfallCategoryId('S', 3), "Ontology Clarity");
        create(id2category, new PitfallCategoryId('N', 7), new PitfallCategoryId('S', 3), "Ontology Understanding");
        create(id2category, new PitfallCategoryId('N', 8), new PitfallCategoryId('S', 3), "Ontology Metadata");
        create(id2category, new PitfallCategoryId('C', 2), new PitfallCategoryId('H', 0),
                "Classification by Evaluation Criteria");
        create(id2category, new PitfallCategoryId('S', 4), new PitfallCategoryId('C', 2), "Consistency");
        create(id2category, new PitfallCategoryId('S', 5), new PitfallCategoryId('C', 2), "Completeness");
        create(id2category, new PitfallCategoryId('S', 6), new PitfallCategoryId('C', 2), "Consciseness");
        ID2CATEGORY = Collections.unmodifiableMap(id2category);
    }

    private final PitfallCategoryId id;
    private final PitfallCategory parent;
    private final String title;
    // private final String description;

    private PitfallCategory(final PitfallCategoryId id, final PitfallCategory parent, final String title) {
        this.id = id;
        this.parent = parent;
        this.title = title;
        // this.description = description;
    }

    private static PitfallCategory create(final Map<PitfallCategoryId, PitfallCategory> id2category,
            final PitfallCategoryId id, final PitfallCategoryId parentId, final String title) {

        final PitfallCategory parent;
        if (parentId == null) {
            parent = null;
        } else {
            parent = id2category.get(parentId);
            if (parent == null) {
                throw new IllegalStateException(
                        "Pitfall categories need to be created from the tree top to the bottom. "
                                + "Tried to create %s with parent %s (which does not (yet) exist).");
            }
        }
        final PitfallCategory category = new PitfallCategory(id, parent, title);
        id2category.put(id, category);
        return category;
    }

    public static void main(final String... args) {

        final Map<PitfallCategoryId, Set<PitfallCategory.TreeChild>> pfCatTree = PitfallCategory.getTree();
        final PitfallCategory.TreeChild root = pfCatTree.get(null).iterator().next();
        System.out.println(root);
        for (final PitfallCategory.TreeChild cNode : pfCatTree.get(root.getCategory())) {
            System.out.println("\t" + cNode);
            for (final PitfallCategory.TreeChild sNode : pfCatTree.get(cNode.getCategory())) {
                final PitfallCategoryId sCatId = sNode.getCategory();
                final PitfallCategory sCat = PitfallCategory.get(sCatId);
                final String sCatTitle = Utils.urlEncode(sCat.getTitle());
                System.out.println("\t\t" + sNode);
                for (final PitfallCategory.TreeChild nNode : pfCatTree.get(sCatId)) {
                    System.out.println("\t\t\t" + nNode);
                    final PitfallCategoryId nCatId = nNode.getCategory();
                }
            }
        }
    }

    private static Map<PitfallCategoryId, Set<TreeChild>> buildTree() {

        final Map<PitfallCategoryId, Set<TreeChild>> tree = new HashMap<>();
        for (final PitfallCategory pCat : ID2CATEGORY.values()) {
            final PitfallCategoryId parentId = pCat.getParent() == null ? null : pCat.getParent().getId();
            treePut(tree, parentId, new TreeChild(pCat.getId()));
        }
        for (final PitfallInfo pInfo : CheckersCatalogue.getPitfallInfosAll()) {
            final TreeChild child = new TreeChild(pInfo.getId());
            for (final PitfallCategory pCat : pInfo.getCategory()) {
                treePut(tree, pCat.getId(), child);
            }
        }
        for (final Map.Entry<PitfallCategoryId, Set<TreeChild>> treeEntry : tree.entrySet()) {
            treeEntry.setValue(Collections.unmodifiableSet(treeEntry.getValue()));
        }

        return tree;
    }

    private static void treePut(final Map<PitfallCategoryId, Set<TreeChild>> tree, final PitfallCategoryId id,
            final TreeChild child) {
        final Set<TreeChild> children;
        if (tree.containsKey(id)) {
            children = tree.get(id);
        } else {
            children = new TreeSet<>();
            tree.put(id, children);
        }
        children.add(child);
    }

    public static PitfallCategory get(final PitfallCategoryId id) {
        return ID2CATEGORY.get(id);
    }

    public static Map<PitfallCategoryId, Set<TreeChild>> getTree() {
        if (PARENT2CHILDREN == null) {
            PARENT2CHILDREN = Collections.unmodifiableMap(buildTree());
        }
        return PARENT2CHILDREN;
    }

    public static Set<PitfallId> getLeaves(final PitfallCategoryId top) {

        final Set<PitfallId> leaves = new HashSet<>();
        final Stack<PitfallCategoryId> branches = new Stack<>();
        branches.add(top);
        while (!branches.isEmpty()) {
            final PitfallCategoryId curBranch = branches.pop();
            for (final TreeChild node : getTree().get(curBranch)) {
                if (node.isCategory()) {
                    branches.add(node.getCategory());
                } else {
                    leaves.add(node.getPitfall());
                }
            }
        }
        return leaves;
    }

    // public String getDescription() {
    // return description;
    // }

    public PitfallCategoryId getId() {
        return id;
    }

    public PitfallCategory getParent() {
        return parent;
    }

    public String getTitle() {
        return title;
    }
}
