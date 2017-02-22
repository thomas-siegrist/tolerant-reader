package ch.sigi.tolerantreader.model;

import java.util.Arrays;

/**
 * Created by thomas on 22.02.17.
 */
public class ExampleData {

    public static Model getModel() {
        return exampleModel();

    }

    private static Model exampleModel() {
        Model model = new Model();
        model.setSomeText("Some long text .....");
        model.setSomeLong(1L);
        model.setSomeBoolean(false);
        model.setSomeShort((short) 99);
        model.setSomeInteger(10_000);
        model.setSomeDouble(1.1d);
        model.setSomeFloat(1.1f);

        model.setSubTree(exampleSubtree());
        return model;
    }

    private static SubTree exampleSubtree() {
        SubTree subtree = new SubTree();
        subtree.setSomeLong(99L);
        subtree.setSomeText("Subtree-Text");
        subtree.setSomeList(Arrays.asList("Element1", "Element2", "Element3"));
        return subtree;
    }

}
