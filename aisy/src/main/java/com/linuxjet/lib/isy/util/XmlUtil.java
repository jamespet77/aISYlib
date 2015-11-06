package com.linuxjet.lib.isy.util;

/**
 * Created by jamespet on 10/15/15.
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import org.w3c.dom.*;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class XmlUtil {
  private XmlUtil(){}

  public static List<Node> asList(NodeList n) {
    return n.getLength()==0?
        Collections.<Node>emptyList(): new NodeListWrapper(n);
  }

  public static List<Node> asList(NamedNodeMap n) {
    return n.getLength()==0?
        Collections.<Node>emptyList(): new NodeListWrapper(n);
  }

  static final class NodeListWrapper extends AbstractList<Node>
      implements RandomAccess {
    private final NodeList list;
    private final NamedNodeMap list2;


    NodeListWrapper(NodeList l) {
      list=l;
      list2=null;
    }

    NodeListWrapper(NamedNodeMap l) {
      list=null;
      list2=l;
    }

    public Node get(int index) {
      if (list==null)
        return list2.item(index);
      return list.item(index);
    }

    public int size() {
      if (list == null)
        return list2.getLength();
      return list.getLength();
    }
  }


  public static String getStringFromInputStream(InputStream stream) throws IOException {
    int n = 0;
    char[] buffer = new char[2048 * 4];
    InputStreamReader reader = new InputStreamReader(stream);
    StringWriter writer = new StringWriter();
    while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
    reader.close();
    return writer.toString();
  }

  public static final String prettyPrint(Document xml) throws Exception {
    Transformer tf = TransformerFactory.newInstance().newTransformer();
    tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    tf.setOutputProperty(OutputKeys.INDENT, "yes");
    Writer out = new StringWriter();
    tf.transform(new DOMSource(xml), new StreamResult(out));
    return out.toString();
  }



}