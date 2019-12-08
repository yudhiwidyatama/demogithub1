import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

class Main {
    public static class Graphml {

        public static String encodeXML(CharSequence s) {
            StringBuilder sb = new StringBuilder();
            int len = s.length();
            for (int i=0;i<len;i++) {
                int c = s.charAt(i);
                if (c >= 0xd800 && c <= 0xdbff && i + 1 < len) {
                    c = ((c-0xd7c0)<<10) | (s.charAt(++i)&0x3ff);    // UTF16 decode
                }
                if (c < 0x80) {      // ASCII range: test most common case first
                    if (c < 0x20 && (c != '\t' && c != '\r' && c != '\n')) {
                        // Illegal XML character, even encoded. Skip or substitute
                        sb.append("&#xfffd;");   // Unicode replacement character
                    } else {
                        switch(c) {
                            case '&':  sb.append("&amp;"); break;
                            case '>':  sb.append("&gt;"); break;
                            case '<':  sb.append("&lt;"); break;
                            // Uncomment next two if encoding for an XML attribute
//                  case '\''  sb.append("&apos;"); break;
//                  case '\"'  sb.append("&quot;"); break;
                            // Uncomment next three if you prefer, but not required
//                  case '\n'  sb.append("&#10;"); break;
//                  case '\r'  sb.append("&#13;"); break;
//                  case '\t'  sb.append("&#9;"); break;

                            default:   sb.append((char)c);
                        }
                    }
                } else if ((c >= 0xd800 && c <= 0xdfff) || c == 0xfffe || c == 0xffff) {
                    // Illegal XML character, even encoded. Skip or substitute
                    sb.append("&#xfffd;");   // Unicode replacement character
                } else {
                    sb.append("&#x");
                    sb.append(Integer.toHexString(c));
                    sb.append(';');
                }
            }
            return sb.toString();

        }
        public static void WriteCsv(List<Node> nodes,List<Allocation> allocations,OutputStream outputStream) throws IOException
        {
            OutputStreamWriter o = new OutputStreamWriter(outputStream);
            o.write("##\n" +
                    "## Example CSV import. Use ## for comments and # for configuration. Paste CSV below.\n" +
                    "## The following names are reserved and should not be used (or ignored):\n" +
                    "## id, tooltip, placeholder(s), link and label (see below)\n" +
                    "##\n" +
                    "#\n" +
                    "## Node label with placeholders and HTML.\n" +
                    "## Default is '%name_of_first_column%'.\n" +
                    "#\n" +
                    "# label: %shortname%<br><i style=\"color:gray;\">%name%</i><br>%driver%\n" +
                    "#\n" +
                    "## Node style (placeholders are replaced once).\n" +
                    "## Default is the current style for nodes.\n" +
                    "#\n" +
                    "# style: label;iwhiteSpace=wrap;html=1;rounded=1\n" +
                    "#\n" +
                    "## Parent style for nodes with child nodes (placeholders are replaced once).\n" +
                    "#\n" +
                    "# parentstyle: swimlane;whiteSpace=wrap;html=1;childLayout=stackLayout;horizontal=1;horizontalStack=0;resizeParent=1;resizeLast=0;collapsible=1;\n" +
                    "#\n" +
                    "## Optional column name that contains a reference to a named style in styles.\n" +
                    "## Default is the current style for nodes.\n" +
                    "#\n" +
                    "# stylename: -\n" +
                    "#\n" +
                    "## JSON for named styles of the form {\"name\": \"style\", \"name\": \"style\"} where style is a cell style with\n" +
                    "## placeholders that are replaced once.\n" +
                    "#\n" +
                    "# styles: -\n" +
                    "#\n" +
                    "## Optional column name that contains a reference to a named label in labels.\n" +
                    "## Default is the current label.\n" +
                    "#\n" +
                    "# labelname: -\n" +
                    "#\n" +
                    "## JSON for named labels of the form {\"name\": \"label\", \"name\": \"label\"} where label is a cell label with\n" +
                    "## placeholders.\n" +
                    "#\n" +
                    "# labels: -\n" +
                    "#\n" +
                    "## Uses the given column name as the identity for cells (updates existing cells).\n" +
                    "## Default is no identity (empty value or -).\n" +
                    "#\n" +
                    "# identity: -\n" +
                    "#\n" +
                    "## Uses the given column name as the parent reference for cells. Default is no parent (empty or -).\n" +
                    "## The identity above is used for resolving the reference so it must be specified.\n" +
                    "#\n" +
                    "# parent: -\n" +
                    "#\n" +
                    "## Adds a prefix to the identity of cells to make sure they do not collide with existing cells (whose\n" +
                    "## IDs are numbers from 0..n, sometimes with a GUID prefix in the context of realtime collaboration).\n" +
                    "## Default is csvimport-.\n" +
                    "#\n" +
                    "# namespace: csvimport-\n" +
                    "#\n" +
                    "## Connections between rows (\"from\": source colum, \"to\": target column).\n" +
                    "## Label, style and invert are optional. Defaults are '', current style and false.\n" +
                    "## In addition to label, an optional fromlabel and tolabel can be used to name the column\n" +
                    "## that contains the text for the label in the edges source or target (invert ignored).\n" +
                    "## The label is concatenated in the form fromlabel + label + tolabel if all are defined.\n" +
                    "## The target column may contain a comma-separated list of values.\n" +
                    "## Multiple connect entries are allowed.\n" +
                    "#\n" +
                    "# connect: {\"from\": \"allocto\", \"to\": \"shortname\", \"invert\": false, \"label\": \"allocation\", \\\n" +
                    "#          \"style\": \"curved=1;endArrow=blockThin;endFill=1;fontSize=11;\"}\n" +
                    "# connect: {\"from\": \"parent\", \"to\": \"shortname\", \"invert\": false, \"label\": \"child of\", \\\n" +
                    "#          \"style\": \"curved=1;endArrow=blockThin;endFill=1;fontSize=11;\"}\n" +
                    "#\n" +
                    "## Node x-coordinate. Possible value is a column name. Default is empty. Layouts will\n" +
                    "## override this value.\n" +
                    "#\n" +
                    "# left: \n" +
                    "#\n" +
                    "## Node y-coordinate. Possible value is a column name. Default is empty. Layouts will\n" +
                    "## override this value.\n" +
                    "#\n" +
                    "# top: \n" +
                    "#\n" +
                    "## Node width. Possible value is a number (in px), auto or an @ sign followed by a column\n" +
                    "## name that contains the value for the width. Default is auto.\n" +
                    "#\n" +
                    "# width: auto\n" +
                    "#\n" +
                    "## Node height. Possible value is a number (in px), auto or an @ sign followed by a column\n" +
                    "## name that contains the value for the height. Default is auto.\n" +
                    "#\n" +
                    "# height: auto\n" +
                    "#\n" +
                    "## Padding for autosize. Default is 0.\n" +
                    "#\n" +
                    "# padding: -12\n" +
                    "#\n" +
                    "## Comma-separated list of ignored columns for metadata. (These can be\n" +
                    "## used for connections and styles but will not be added as metadata.)\n" +
                    "#\n" +
                    "# ignore: id,image,fill,stroke,refs,manager\n" +
                    "#\n" +
                    "## Column to be renamed to link attribute (used as link).\n" +
                    "#\n" +
                    "# link: url\n" +
                    "#\n" +
                    "## Spacing between nodes. Default is 40.\n" +
                    "#\n" +
                    "# nodespacing: 40\n" +
                    "#\n" +
                    "## Spacing between levels of hierarchical layouts. Default is 100.\n" +
                    "#\n" +
                    "# levelspacing: 100\n" +
                    "#\n" +
                    "## Spacing between parallel edges. Default is 40. Use 0 to disable.\n" +
                    "#\n" +
                    "# edgespacing: 40\n" +
                    "#\n" +
                    "## Name or JSON of layout. Possible values are auto, none, verticaltree, horizontaltree,\n" +
                    "## verticalflow, horizontalflow, organic, circle or a JSON string as used in Layout, Apply.\n" +
                    "## Default is auto.\n" +
                    "#\n" +
                    "# layout: auto\n" +
                    "#\n" +
                    "## ---- CSV below this line. First line are column names. ----\n"
                    );
            o.write("shortname,name,allocto,driver,parent\n");
            HashSet<Allocation> allocs = new HashSet<>();
            allocs.addAll(allocations);
            for (Node n: nodes)
            {
                StringBuilder sb = new StringBuilder();
                sb.append(n.id.replaceAll(",",".")); sb.append( ",");
                sb.append(n.longName.replaceAll(",","."));
                sb.append( ",");

                if ((n.outgoingAlloc!=null) && (allocs.contains(n.outgoingAlloc)))
                    {
                        sb.append("\"");
                        boolean notfirst = false;
                        for (Node toNode : n.outgoingAlloc.toNodes) {
                            if (notfirst) sb.append(",");
                            notfirst = true;
                            sb.append(toNode.id.replaceAll(",", "."));
                        }
                        sb.append("\"");
                        sb.append(",");
                        sb.append("&lt;");
                        sb.append(n.outgoingAlloc.driver);
                        sb.append("&gt;");
                        sb.append(",");
                    } else sb.append(",-,");

                o.write(sb.toString());
                o.write("\n");
                for (Node childNode : n.childSet)
                {
                    StringBuilder sb1 = new StringBuilder();
                    sb1.append(childNode.id.replaceAll(",","."));
                    sb1.append( ",");
                    sb1.append(childNode.longName.replaceAll(",","."));
                    sb1.append( ",,");
                    sb1.append( "CostElement,");
                    sb1.append(n.id.replaceAll(",","."));
                    o.write(sb1.toString());
                    o.write("\n");

                }
            }

            o.close();
        }
        public static void Write(List<Node> nodes,List<Allocation> allocations,OutputStream outputStream) throws IOException
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                    "<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"\n" +
                    "           xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                    "           xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml http://www.yworks.com/xml/schema/graphml/1.0/ygraphml.xsd\"\n" +
                    "           xmlns:y=\"http://www.yworks.com/xml/graphml\">\n" +
                    "    <key id=\"d0\" for=\"node\" yfiles.type=\"nodegraphics\"/>\n" +
                    "    <key id=\"d1\" for=\"edge\" yfiles.type=\"edgegraphics\"/>");
            outputStreamWriter.write("<graph id=\"G\" edgedefault=\"undirected\">\n" +
                    " ");

            for (Node n: nodes)
            {
                StringBuilder sb = new StringBuilder();
                sb.append("<node id=\""); sb.append(n.id); sb.append( "\">");
                sb.append("<data key=\"d0\" >\n" +
                        "            <y:ShapeNode>\n" +
                        "              <y:Fill color=\"#CCCCFF\"  transparent=\"false\"/>\n" +
                        "              <y:BorderStyle type=\"line\" width=\"1.0\" color=\"#000000\"/>\n" +
                        "              <y:NodeLabel visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" textColor=\"#000000\" modelName=\"internal\" modelPosition=\"c\" autoSizePolicy=\"center\">" +
                        encodeXML(n.id) + "\n" + encodeXML(n.longName) +
                        "</y:NodeLabel>\n" +
                        "              <y:Shape type=\"roundrectangle\"/>\n" +
                        "            </y:ShapeNode>\n" +
                        "          </data>");
                sb.append("</node>\n");
                outputStreamWriter.append(sb.toString());
            }

            for (Allocation allocation : allocations)
            {
                for (Node destNode : allocation.toNodes) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("<edge source=\"");
                    sb.append(allocation.from.id);
                    sb.append("\" directed=\"true\" target=\"");
                    sb.append(destNode.id);
                    sb.append("\" >\n");
                    sb.append("<data key=\"d1\">\n" +
                            "              <y:PolyLineEdge>\n" +
                            "                <y:LineStyle type=\"line\" width=\"1.0\" color=\"#000000\"/>\n" +
                            "                <y:Arrows source=\"none\" target=\"standard\"/>\n" +
                            "                <y:EdgeLabel visible=\"true\" alignment=\"center\" fontFamily=\"Dialog\" fontSize=\"12\" fontStyle=\"plain\" textColor=\"#000000\" modelName=\"free\" modelPosition=\"anywhere\" preferredPlacement=\"target\" distance=\"2.0\" ratio=\"0.5\">" +
                            encodeXML(allocation.driver()) +
                            "</y:EdgeLabel>\n" +
                            "                <y:BendStyle smoothed=\"false\"/>\n" +
                            "              </y:PolyLineEdge>\n" +
                            "            </data>");
                    sb.append("</edge>");
                    outputStreamWriter.append(sb.toString());
                }
            }
            outputStreamWriter.append("</graph></graphml>");
            outputStreamWriter.close();

        }
    }
    public static class Allocation {
        Node from;
        ArrayList<Node> toNodes = new ArrayList<Node>();
        String driver;
        public String driver()
        {
            if (driver==null) return "-"; return driver;
        }
        @Override
        public String toString() {
            StringBuilder toNodebuf = new StringBuilder();
            for (Node n : toNodes) {
                toNodebuf.append("   ");
                toNodebuf.append(n.toString());
                toNodebuf.append("\r\n");
            }
            return "Allocation{" +
                    "from=" + from +
                    ", driver='" + driver + '\'' +
                    ", toNodes=\r\n" + toNodebuf.toString() +
                    '}';
        }
    }
    public static class Node implements Comparable {
        String id;
        String longName;
        String type;
        ArrayList<Allocation> incomingAlloc = new ArrayList<Allocation>();
        Set<Object> requiredObjects = null;
        List<Object> requiredList = new ArrayList<>();
        Set<Node> childSet = new TreeSet<>();
        Allocation outgoingAlloc;
        Node parent;
        public void setParent(Node p)
        {
            if (p==null) return;
            parent = p;
            p.childSet.add(this);
        }
        public boolean isSource() {
            return incomingAlloc.size()==0;
        }
        public boolean isSink() {
            return outgoingAlloc == null;
        }
        public Node(String id, String longName, String type) {
            this.id = id;
            this.longName = longName;
            this.type = type;
        }

        public String toStringFull() {
            if (isSource()) {
                StringBuilder s= new StringBuilder("Node{Source," +
                        "id='" + id + '\'' +
                        ", longName='" + longName + '\'' +
                        ", type='" + type + '\'' +
                        '}');
                if (this.childSet.size()>0)
                {
                    for (Node ch : childSet)
                        s.append("\n  "+ch.toString());
                }
                return s.toString();
            }
            return "Node{" +
                    "id='" + id + '\'' +
                    ", longName='" + longName + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
        @Override
        public String toString() {
            if (isSource()) {
                StringBuilder s= new StringBuilder("Node{Source," +
                        "id='" + id + '\'' +
                        ", longName='" + longName + '\'' +
                        ", type='" + type + '\'' +
                        '}');

                return s.toString();
            }
            return "Node{" +
                    "id='" + id + '\'' +
                    ", longName='" + longName + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }

        public void fillRequirement()
        {
            if (requiredObjects != null) return;
            requiredObjects = new HashSet<>();
            requiredObjects.add(this);
            requiredList.add(this);
            for (Allocation alloc: incomingAlloc)
            {
                if (requiredObjects.add(alloc))
                    requiredList.add(alloc);

                if (requiredObjects.add(alloc.from)) requiredList.add(alloc.from);
                for (Node nodeTo  : alloc.toNodes)
                {
                    if (requiredObjects.add(nodeTo)) requiredList.add(nodeTo);
                }
                alloc.from.fillRequirement();
                for (Object obj : alloc.from.requiredList)
                    if (requiredObjects.add(obj)) requiredList.add(obj);
            }

        }

        @Override
        public int compareTo(Object o) {
            Node other = (Node)o;
            return this.id.compareTo(other.id);
        }
    }
    HashMap<String,Node> nodeList = new HashMap<String,Node>();
    public void createNodeIfNotExists(String id, String name, String type) {
        if (!nodeList.containsKey(id))
            nodeList.put(id, new Node(id, name,type));
    }
    public static void main(String[] args)
    {
        Main m = new Main();

	try
    {
        for (int fileIdx=1; fileIdx<args.length; fileIdx++) {
            Scanner s = new Scanner(new FileReader(new File(args[fileIdx])));
            String nottabS = "[^\\t]+";
            String delimitS = "\\t|\\r?\\n|\\n";
            String delimitS2 = "[\\t]";
            s.useDelimiter(delimitS);
            Pattern nottab = Pattern.compile(nottabS);
            String prefixStr = "";
            String headerStr[] = new String[6];
            ArrayList<Node> nodePath =  new ArrayList<>();
            String defaultType = "";
            for (int prefix = 1; prefix <= 5; prefix++) headerStr[prefix] = s.next();
            defaultType = headerStr[4];
            prefixStr = headerStr[5];
            System.out.println("File Type = " + prefixStr);
            if (prefixStr.equals("STRUCTURES_BY_LEVEL")) {
                s.useDelimiter(delimitS);
                while (s.hasNext()) {
                    String fieldId = s.next();
                    if (fieldId.length() == 0) break;
                    System.out.print(fieldId);
                    System.out.print("\t");
                    s.useDelimiter(delimitS2);
                    String fieldLvl = s.next();
                    System.out.print(fieldLvl);
                    int level = Integer.parseInt(fieldLvl);

                    System.out.print("\t");
                    String fieldx = s.next();
                    String fieldDesc = fieldx.replaceAll("\\s+", " ");
                    //String fieldxc = fieldx.replaceAll("\\t|\\r?\\n|\\n"," ");
                    System.out.print(fieldDesc);
                    System.out.print("\t");

                    s.useDelimiter(delimitS);
                    String fieldlas = s.next();
                    System.out.print(fieldlas);
                    System.out.print("\n");
                    if (fieldlas.equals("E")) {
                        fieldId = fieldId + "/E";
                        level = level+1;
                    }
                    if (fieldlas.equals("C")) {
                        fieldId = fieldId + "/C";
                    }
                    m.createNodeIfNotExists(fieldId,fieldDesc,defaultType);
                    Node theNode = m.nodeList.get(fieldId);
                    while (nodePath.size()<(level+1))  nodePath.add(null);
                    nodePath.set(level,theNode);
                    Node parentNode = nodePath.get(level-1);
                    theNode.setParent(parentNode);
                }
            } else if (prefixStr.equals("SOURCE_DESTINATION_ASSIGNMENTS")) {
                s.useDelimiter(delimitS);
                Node srcNode = null;
                Allocation currentAlloc = null;
                while (s.hasNext()) {
                    String field1 = s.next();
                    if (field1.equals("NEW_ASSIGNMENT")) {
                        System.out.println(field1);
                        String fieldSrcId = s.next();
                        System.out.print(fieldSrcId);
                        System.out.print("\t");
                        s.useDelimiter(delimitS2);

                        String fieldSrcName = s.next();
                        String fieldSrcNameC = fieldSrcName.replaceAll("\\s+", " ");
                        //String fieldSrcNameC = fieldSrcName.replaceAll("\\t|\\r?\\n|\\n"," ");
                        System.out.print(fieldSrcNameC);
                        System.out.print("\t");
                        s.useDelimiter(delimitS);
                        String fieldType = s.next();
                        System.out.print(fieldType);
                        System.out.print("\t");
                        String fieldDriver = s.next();
                        m.createNodeIfNotExists(fieldSrcId, fieldSrcNameC, fieldType);
                        srcNode = m.nodeList.get(fieldSrcId);
                        currentAlloc = new Allocation();
                        currentAlloc.from = srcNode;
                        if (srcNode.outgoingAlloc != null) System.err.println("Node: " + srcNode.id +
                                " : doubly allocated (prev driver is : " + srcNode.outgoingAlloc.driver() + ")");
                        srcNode.outgoingAlloc = currentAlloc;
                        currentAlloc.driver = fieldDriver;
                        if (fieldDriver.equals("EVENLY_ASSIGNED") || fieldDriver.equals("PERCENTAGES")) {
                            System.out.print(fieldDriver);
                            System.out.print("\n");
                        } else {

                            for (int i = 0; i < 2; i++) {
                                String fieldx = s.next();
                                String fieldxc = fieldx.replaceAll("\\s+", " ");
                                System.out.print(fieldxc);
                                System.out.print("\t");
                            }
                            s.useDelimiter(delimitS);
                            String fieldlas = s.next();
                            System.out.print(fieldlas);
                            System.out.print("\n");
                        }
                    } else {
                        System.out.print(field1);
                        System.out.print("\t");
                        s.useDelimiter(delimitS2);
                        String fieldx = s.next();
                        String fieldxc = fieldx.replaceAll("\\s+", " ");
                        System.out.print(fieldxc);
                        System.out.print("\t");
                        s.useDelimiter(delimitS);
                        String fieldlas = s.next();
                        System.out.print(fieldlas);
                        System.out.print("\n");
                        m.createNodeIfNotExists(field1, fieldxc, fieldlas);
                        Node destNode = m.nodeList.get(field1);
                        currentAlloc.toNodes.add(destNode);
                        destNode.incomingAlloc.add(currentAlloc);

                    }

                }

            }
        }
        /*
        System.out.println("-----------------------------------------------");
        System.out.println("Sources :" + sourceList.size() +" items");

        for (Node n: sourceList)
        {
            System.out.println(n.toString());
        }
        */
                ArrayList<Node> sinkList, sourceList;
                sinkList = new ArrayList<Node>();
                sourceList = new ArrayList<Node>();
                for (Node n : m.nodeList.values()) {
                    if (n.isSink()) {
                        sinkList.add(n);
                    }
                    if (n.isSource()) {
                        sourceList.add(n);
                    }
                }
                for (Node n: sinkList)
                {
                    n.fillRequirement();
                    if (n.requiredList.size()>1)
                    System.out.println("node " + n.id + " : " + n.requiredList.size()+ " objects required ");
                }
                System.out.println("Please input node id to print :");
                java.util.Scanner scanner = new Scanner(System.in);
                String theNodeId = scanner.next();
                Node theNode = m.nodeList.get(theNodeId);
                ArrayList<Allocation> selAlloc = new ArrayList<>();
                ArrayList<Node> selNodes = new ArrayList<>();

                for (Object o : theNode.requiredList) {
                    if (o instanceof Allocation) {
                        Allocation oAlloc = (Allocation) o;
                        selAlloc.add(oAlloc);

                        System.out.println(oAlloc.toString());
                    }
                    if (o instanceof Node) {
                        Node oNode = (Node) o;
                        selNodes.add(oNode);

                        System.out.println(oNode.toStringFull());
                    }
                }

                File graphFile = new File("drawio.csv");
                FileOutputStream fileOutputStream = new FileOutputStream(graphFile);
                Graphml.WriteCsv(selNodes, selAlloc, fileOutputStream);
                fileOutputStream.close();
                File graphFile2 = new File("yed.graphml");
                FileOutputStream fileOutputStream2 = new FileOutputStream(graphFile2);
                Graphml.Write(selNodes, selAlloc, fileOutputStream2);
                fileOutputStream2.close();




	} catch (IOException x) {
	x.printStackTrace();
	}

}

}
