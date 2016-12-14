package ch.sigi.tolerantreader.document;

import ch.sigi.tolerantreader.exception.DocumentReadException;
import ch.sigi.tolerantreader.model.Node;

/**
 * Created by thomas on 10.12.16.
 */
public interface Document<T> {

    Object evaluatePath(String path, Node node) throws DocumentReadException;

    boolean pathExists(String path);

    Class<T> getModelClass();

    String getRootNodeIdentifier();

    String getPathDelimiter();

    int getIndexOfFirstElementInCollections();

}
