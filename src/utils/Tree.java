package utils;

public class Tree {

    private TreeNode root, cur;

    public Tree() {
        root = new TreeNode(null);
        cur = root;
    }

    public void insert(int pixel, StringBuilder code) {
        TreeNode root = this.root;
        for (int i = 0; i < code.length(); i++) {
            if (code.charAt(i) == '0') {
                if (root.left == null)
                    root.left = new TreeNode(null);
                root = root.left;
            } else {
                if (root.right == null)
                    root.right = new TreeNode(null);
                root = root.right;
            }
        }
        root.pixel = pixel;
    }

    public Integer search(char c) {
        Integer res = null;
        if (c == '0')
            cur = cur.left;
        else
            cur = cur.right;
        if (cur.pixel != null) {
            res = cur.pixel;
            cur = root;
        }
        return res;
    }

//    public boolean dfs(TreeNode node) {
//        if (node == null)
//            return true;
//        if (node.pixel != null)
//            return node.left == null && node.right == null;
//        if (!dfs(node.left))
//            return false;
//        if (!dfs(node.right))
//            return false;
//        return true;
//    }

    class TreeNode {
        TreeNode left, right;
        Integer pixel;
        TreeNode(Integer pixel) {
            this.pixel = pixel;
        }
    }
}
