import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

class TreeNode {
	public TreeNode parent;
	public TreeNode left;
	public TreeNode right;
	public String content;
	public String type;
	public String position;
	public int numOfNot;
	public TreeNode(String content, String type) {
		this.parent = null;
		this.left = null;
		this.right = null;
		this.position = null;
		this.content = content;
		this.type = type;
		this.numOfNot = 0;
	}
}

//class TreeListener extends LexerParserBaseListener {
//	public TreeNode root = new TreeNode("dummy", "dummy");
//	private Stack<TreeNode> stack = new Stack<TreeNode>();
//	@Override 
//	public void exitNested(@NotNull LexerParserParser.NestedContext ctx) { 
//		TreeNode right = stack.pop();
//		TreeNode left = stack.pop();
//		TreeNode op = new TreeNode(ctx.op().getText(), "operator");
//		op.left = left;
//		op.right = right;
//		left.parent = op;
//		left.position = "left";
//		right.parent = op;
//		right.position = "right";
//		stack.push(op);
//	}
//	@Override 
//	public void exitNegation(@NotNull LexerParserParser.NegationContext ctx) { 
//		TreeNode op = new TreeNode(ctx.NOT().getText(), "operator");
//		TreeNode left = stack.pop();
//		op.left = left;
//		left.parent = op;
//		left.position = "left";
//		stack.push(op);
//	}
//	@Override 
//	public void exitPred(@NotNull LexerParserParser.PredContext ctx) { 
//		TreeNode cur = new TreeNode(ctx.getText(), "operand");
//		stack.push(cur);
//	}
//	@Override 
//	public void exitR(@NotNull LexerParserParser.RContext ctx) { 
//		TreeNode cur = stack.pop();
//		root.left = cur;
//		cur.parent = root;
//		cur.position = "left";
//	}
//}

class Literal extends Object{
	String content;
	Boolean truth;
	Boolean used;
	List<String> parameters;
	public Literal(String content, Boolean truth, List<String> parameters) {
		this.content = content;
		this.truth = truth;
		this.parameters = parameters;
		this.used = false;
	}
	
	@Override
	public boolean equals(Object obj) {
		Literal b = (Literal) obj;
		if (b.content.equals(this.content) && b.truth == this.truth) {
			return true;
		} else {
			return false;
		}
	}
}

class Sentence extends Object{
	static int count = 0;
	String content;
	boolean used;
	List<Literal> literals;
	int id;
	Set<Integer> combined;
	public Sentence(String content, List<Literal> literals) {
		this.content = content;
		this.literals = literals;
		this.used = false;
		this.id = count++;
		this.combined = new HashSet<Integer>();
	}
	
	@Override
	public boolean equals(Object obj) {
		Sentence b = (Sentence) obj;
		if (b.content.equals(this.content)) {
			return true;
		} else {
			return false;
		}
	}
}

public class LogicQueryHanlder {
	
	static String[] testcase = {
			"(A(x) &     B(x)  )   ",
			"((A(x  )   & B(x)) => C(x)  )  ",
			"(~A(x))",
			"A(x)",
			"(~(~(~(~A(x)))))",
			"A(x,y)",
			"(((~(A(x) & B(x))) | (~C(x, Baadf))) => ((Aa(x) & Bb(x)) | Cc(A, Baadf)))",
			"(A(a) | (B(b) | (C(c) & D(d))))",
			"((A(a) & B(b)) | (C(c) & D(d)))",
			"(((A(a)& B(b)) | C(c)) | D(d))"
	};
	
	static List<String> queryRaw = new ArrayList<String>();
	static List<String> KB = new ArrayList<String>();
	static List<Literal> query_negated = new ArrayList<Literal>();
	static Map<String, List<Literal>> positive = new HashMap<String, List<Literal>>();
	static Map<String, List<Literal>> negative = new HashMap<String, List<Literal>>();
	static Map<String, List<Sentence>> conclusion = new HashMap<String, List<Sentence>>();
	static Map<String, List<Sentence>> premise = new HashMap<String, List<Sentence>>();
	static List<Sentence> allSentences = new ArrayList<Sentence>();
	
	public static void main(String[] args) {
//		testAll();//
		readFile();
		parseKB();
		parseQuery();
		resolution();
//		testTranslate();
//		testMergeSentence();
//		testRemoveLiteral();
//		testOutput();//
	}
	
	private static void readFile() {
//		File file = new File("D:\\input.txt");// Need to be changed to the line below
		File file = new File("input.txt");// Used on Unix
		BufferedReader fbr = null;
		try {
			fbr = new BufferedReader(new FileReader(file));
			String tempString = null;
            int line = 0;
            int numOfQuery = 0;
            int numOfKB = 0;
            try {
				if ((tempString = fbr.readLine()) != null) {//must check line number first
					numOfQuery = Integer.parseInt(tempString);	
				}
				while (line < numOfQuery && ((tempString = fbr.readLine()) != null)) {//must check line number first
					queryRaw.add(tempString);	
				    line++;
				}
				if ((tempString = fbr.readLine()) != null) {//must check line number first
					numOfKB = Integer.parseInt(tempString);	
				}
				line = 0;
				while (line < numOfKB && ((tempString = fbr.readLine()) != null)) {//must check line number first
					KB.add(tempString);	
				    line++;
				}
//				checkParameter();// test
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} finally {
			try {
				if (fbr != null) {
					fbr.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void parseKB() {
		for (int i = 0; i < KB.size(); i++) {
			String data = KB.get(i);
			TreeNode root = build(data);
			TreeNode dummy = new TreeNode("dummy", "dummy");
			dummy.left = root;
			root.parent = dummy;
			root.position = "left";
			convertToCNF(dummy.left);
			storeInMemory(dummy.left , i);
//			Debug(dummy.left);//test
		}
//		debugAllMap();//test
	}
	
	private static TreeNode build(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim();
		if (str.length() == 0 || str.equals("")) {
			return null;
		}
		if (Character.isLetter(str.charAt(0))) {
			TreeNode cur = new TreeNode(str.trim(), "operand");
			return cur;
		}
		String newStr = str.substring(1, str.length() - 1);
		newStr = newStr.trim();
		if (newStr.charAt(0) == '~') {
			TreeNode op = new TreeNode("~", "operator");
			TreeNode left = build(newStr.substring(1, newStr.length()).trim());
			op.left = left;
			if (left != null) {
				left.parent = op;
				left.position = "left";
			}
			return op;
		}
		int leftP = 0;
		int rightP = 0;
		int index = 0;
		char[] characters = newStr.toCharArray();
		for (int i = 0; i < characters.length; i++) {
			char ch = characters[i];
			if (ch == '(') {
				leftP++;
			} else if (ch == ')') {
				rightP++;
			} else if (ch == '|' || ch == '&' || ch == '=') {
				if (leftP == rightP) {
					index = i;
					break;
				}
			} else {
				continue;
			}
		}
		TreeNode right = null;
		TreeNode left = null;
		TreeNode op = null;
		if (newStr.charAt(index) != '=') {
			right = build(newStr.substring(index + 1).trim());
			left = build(newStr.substring(0, index).trim());
			op = new TreeNode(newStr.substring(index, index + 1).trim(), "operator");
		} else {
			right = build(newStr.substring(index + 2).trim());
			left = build(newStr.substring(0, index).trim());
			op = new TreeNode(newStr.substring(index, index + 2).trim(), "operator");
		}
		op.left = left;
		op.right = right;
		if (left != null) {
			left.parent = op;
			left.position = "left";
		}
		if (right != null) {
			right.parent = op;
			right.position = "right";
		}
		return op;
	}
	
	private static void parseQuery() {
		for (int i = 0; i < queryRaw.size(); i++) {
			String origin = queryRaw.get(i).trim();
			String content = origin.substring(0, origin.indexOf("(") + 1).trim();
			Boolean truth;
			if (content.contains("~")) {
				truth = false;
				content = content.substring(1).trim();
			} else {
				truth = true;
			}
			List<String> parameters = new ArrayList<String>();
			String parameterContent = origin.substring(origin.indexOf("(") + 1, origin.indexOf(")")).trim();
			String[] parametersTmp = parameterContent.split(",");
			for (String parameter : parametersTmp) {
				parameters.add(parameter.trim());
				content += parameter.trim() + ",";
			}
			content = content.substring(0, content.length() - 1);
			content += ")";
			Literal literal = new Literal(content, truth, parameters);
			if (literal.truth) {
				literal.truth = false;
			} else {
				literal.truth = true;
			}
			query_negated.add(literal);
		}
//		debugQueryNegated();//test
	}
	
	private static void resolution() {
		List<String> results = new ArrayList<String>();
		for (Literal item : query_negated) {
			//rollbackKB();
//			if (item.content.equals("Ancestor(Liz,Bob)")) {
//				results.add("FALSE");
//			} else {
				allSentences.clear();
				addSentenceToQueue();
				List<Literal> list = new ArrayList<Literal>();
				list.add(item);
				String content = item.content;
				if (!item.truth) {
					content = "~" + content;
				}
				Sentence query = new Sentence(content, list);
				if (allSentences.contains(query)) {
					System.out.println("1");
					results.add("FALSE");
				} else {
					allSentences.add(query);
					Set<String> visited = new HashSet<String>();
					createVisited(visited);
					int value = resolutionHelper(visited);
					System.out.println(value);
					if (value != -1) {
						results.add("FALSE");
					} else {
						results.add("TRUE");
					}
				}
//			}
		}
		outputFile(results);
	}



	private static void addSentenceToQueue() {
		for (Map.Entry<String, List<Literal>> entry : positive.entrySet()) {
			for (Literal item : entry.getValue()) {
				Literal newLiteral = literalClone(item);
				String sentenceContent = new String(newLiteral.content);
				if (!newLiteral.truth) {
					sentenceContent = "~" + sentenceContent;
				}
				List<Literal> list = new ArrayList<Literal>();
				list.add(newLiteral);
				Sentence newSentence = new Sentence(sentenceContent, list);
				allSentences.add(newSentence);
			}
		}
		for (Map.Entry<String, List<Literal>> entry : negative.entrySet()) {
			for (Literal item : entry.getValue()) {
				Literal newLiteral = literalClone(item);;
				String sentenceContent = new String(newLiteral.content);
				if (!newLiteral.truth) {
					sentenceContent = "~" + sentenceContent;
				}
				List<Literal> list = new ArrayList<Literal>();
				list.add(newLiteral);
				Sentence newSentence = new Sentence(sentenceContent, list);
				allSentences.add(newSentence);
			}
		}
		for (Map.Entry<String, List<Sentence>> entry : premise.entrySet()) {
			for (Sentence sentence : entry.getValue()) {
				String sentenceContent = new String(sentence.content);
				List<Literal> literals = new ArrayList<Literal>();
				for (Literal item : sentence.literals) {
					Literal newLiteral = literalClone(item);
					literals.add(newLiteral);
				}
				Sentence newSentence = new Sentence(sentenceContent, literals);
				if (!allSentences.contains(newSentence)) {
					allSentences.add(newSentence);
				}
			}
		}
		for (Map.Entry<String, List<Sentence>> entry : conclusion.entrySet()) {
			for (Sentence sentence : entry.getValue()) {
				String sentenceContent = new String(sentence.content);
				List<Literal> literals = new ArrayList<Literal>();
				for (Literal item : sentence.literals) {
					Literal newLiteral = literalClone(item);
					literals.add(newLiteral);
				}
				Sentence newSentence = new Sentence(sentenceContent, literals);
				if (!allSentences.contains(newSentence)) {
					allSentences.add(newSentence);
				}
			}
		}
	}
	
	private static Literal literalClone(Literal item) {
		String literalContent = new String(item.content);
		List<String> params = new ArrayList<String>();
		for (String param : item.parameters) {
			params.add(new String(param));
		}
		boolean literalTruth = item.truth;
		Literal newLiteral = new Literal(literalContent, literalTruth, params);
		return newLiteral;
	}
	
	private static void createVisited(Set<String> visited) {
		for (Sentence item : allSentences) {
			visited.add(item.content);
//			System.out.println(item.content);
		}
	}
	
	private static void rollbackKB() {
		// TODO Auto-generated method stub
		for (Map.Entry<String, List<Literal>> entry : positive.entrySet()) {
			for (Literal item : entry.getValue()) {
				item.used = false;
			}
		}
		for (Map.Entry<String, List<Literal>> entry : negative.entrySet()) {
			for (Literal item : entry.getValue()) {
				item.used = false;
			}
		}
		for (Map.Entry<String, List<Sentence>> entry : conclusion.entrySet()) {
			for (Sentence item : entry.getValue()) {
				item.used = false;
			}
		}
		for (Map.Entry<String, List<Sentence>> entry : premise.entrySet()) {
			for (Sentence item : entry.getValue()) {
				item.used = false;
			}
		}
	}

	private static void convertToCNF(TreeNode root) {
		removeImplication(root);
		removeNegation(root);
		distributedOr(root);
	}
	
	private static void removeImplication(TreeNode root) {
		if (root == null || root.type.equals("operand")) {
			return;
		}
		if (root.type.equals("operator") && root.content.equals("=>")) {
			root.content = "|";
			TreeNode not = new TreeNode("~", "operator");
			not.left = root.left;
			root.left = not;
			not.parent = root;
			not.position = "left";
		}
		removeImplication(root.left);
		removeImplication(root.right);
	}
	
	private static void removeNegation(TreeNode root) {
		countNegation(root, 0);
		removeHelper(root);
	}
	
	private static void countNegation(TreeNode root, int num) {
		if (root == null) {
			return;
		}
		if (root.type.equals("operator") && root.content.equals("~")) {
			countNegation(root.left, ++num);
		} else {
			root.numOfNot = num % 2;
			if (root.numOfNot != 0) {
				if (root.type.equals("operator")) {
					if (root.content.equals("|")) {
						root.content = "&";
					} else {
						root.content = "|";
					}
				} else {
					root.content = "~" + root.content;
				}
			}
			countNegation(root.left, num);
			countNegation(root.right, num);
		}
		return;
	}
	
	private static void removeHelper(TreeNode root) {
		if (root == null || root.type.equals("operand")) {
			return;
		}
		if (root.type.equals("operator") && !root.content.equals("~")) {
			removeHelper(root.left);
			removeHelper(root.right);
		} else {
			TreeNode parent = root.parent;
			String position = root.position;
			while (root.left != null && root.left.content.equals("~")) {
				root = root.left;
			}
			root.left.parent = parent;
			if (parent != null) {
				if (position.equals("left")) {
					parent.left = root.left;
					root.left.position = "left";
				} else {
					parent.right = root.left;
					root.left.position = "right";
				}
			}
			root = root.left;
			removeHelper(root);
		}
		return;
	}
	
	private static void distributedOr(TreeNode root) {
		if (root == null) {
			return;
		}
		if (root.type.equals("operand")) {
			return;
		}
		distributedOr(root.left);
		distributedOr(root.right);
		if (isConflict(root, root.left)) {
			root.content = "&";
			TreeNode leftOr = new TreeNode("|", "operator");
			TreeNode rightOr = new TreeNode("|", "operator");
			TreeNode leftLeftChild = root.left.left;
			TreeNode leftRightChild = root.left.right;
			TreeNode cloneRight = clone(root.right);
			
			leftOr.left = leftLeftChild;
			leftLeftChild.parent = leftOr;
			leftLeftChild.position = "left";
			leftOr.right = root.right;
			root.right.position = "right";
			root.right.parent = leftOr;
			
			rightOr.left = leftRightChild;
			leftRightChild.parent = rightOr;
			leftRightChild.position = "left";
			rightOr.right = cloneRight;
			cloneRight.parent = rightOr;
			cloneRight.position = "right";
			
			root.left = leftOr;
			leftOr.parent = root;
			leftOr.position = "left";
			
			root.right = rightOr;
			rightOr.parent = root;
			rightOr.position = "right";
			
			distributedOr(root.left);
			distributedOr(root.right);
		} else if (isConflict(root, root.right)) {
			root.content = "&";
			TreeNode leftOr = new TreeNode("|", "operator");
			TreeNode rightOr = new TreeNode("|", "operator");
			TreeNode rightLeftChild = root.right.left;
			TreeNode rightRightChild = root.right.right;
			TreeNode cloneLeft = clone(root.left);
			
			leftOr.left = root.left;
			root.left.position = "left";
			root.left.parent = leftOr;
			leftOr.right = rightLeftChild;
			rightLeftChild.parent = leftOr;
			rightLeftChild.position = "right";
			
			rightOr.left = cloneLeft;
			cloneLeft.parent = rightOr;
			cloneLeft.position = "left";
			rightOr.right = rightRightChild;
			rightRightChild.parent = rightOr;
			rightRightChild.position = "right";
			
			root.left = leftOr;
			leftOr.parent = root;
			leftOr.position = "left";
			
			root.right = rightOr;
			rightOr.parent = root;
			rightOr.position = "right";
			
			distributedOr(root.left);
			distributedOr(root.right);
		}
		return;
	}

	private static boolean isConflict(TreeNode A, TreeNode B) {
		if (A.type.equals("operator") && A.content.equals("|") && B.type.equals("operator") && B.content.equals("&")) {
			return true;
		}
		return false;
	}
	
	private static TreeNode clone(TreeNode root) {
		Map<TreeNode, TreeNode> map = new HashMap<TreeNode, TreeNode>();
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		queue.offer(root);
		while (!queue.isEmpty()) {
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				TreeNode cur = queue.poll();
				TreeNode newCur = new TreeNode(cur.content, cur.type);
				map.put(cur, newCur);
				if (cur.left != null) {
					queue.offer(cur.left);
				}
				if (cur.right != null) {
					queue.offer(cur.right);
				}
			}
		}
		for (Map.Entry<TreeNode, TreeNode> entry : map.entrySet()) {
			TreeNode cur = entry.getKey();
			TreeNode newCur = entry.getValue();
			if (map.containsKey(cur.parent)) {
				TreeNode newCurParent = map.get(cur.parent);
				newCur.parent = newCurParent;
				if (cur.position.equals("left")) {
					newCur.position = "left";
					newCurParent.left = newCur;
				} else {
					newCur.position = "right";
					newCurParent.right = newCur;
				}
			} else {
				newCur.parent = null;
				newCur.position = null;
			}
		}
		return map.get(root);
	}
	
	private static void storeInMemory(TreeNode root, int index) {
		StringBuilder sb = new StringBuilder();
		inOrderTraverse(root, sb);
		String plainText = sb.toString().trim();// A & B & C
		String[] sentences = plainText.split("&");
		for (int i = 0; i < sentences.length; i++) {
			String curText = sentences[i].trim();
			storeHelper(curText, index);
		}
	}
	
	private static void inOrderTraverse(TreeNode root, StringBuilder sb) {
		if (root == null) {
			return;
		}
		if (root.type.equals("operand")) {
			sb.append(root.content);
			return;
		}
		inOrderTraverse(root.left, sb);
		sb.append(root.content);
		inOrderTraverse(root.right, sb);
		return;
	}
	
	private static void storeHelper(String curText, int index) {
		String[] literals = curText.split("\\|");
		if (literals.length == 1) {
			String content = literals[0].trim().substring(0, literals[0].trim().indexOf("(") + 1).trim();
			Boolean truth;
			if (content.contains("~")) {
				truth = false;
				content = content.substring(1).trim();
			} else {
				truth = true;
			}
			List<String> parameters = new ArrayList<String>();
			String parameterContent = literals[0].trim().substring(literals[0].trim().indexOf("(") + 1, literals[0].trim().indexOf(")")).trim();
			String[] parametersTmp = parameterContent.split(",");
			for (String parameter : parametersTmp) {
				parameter = parameter.trim();
				content += parameter + ",";
				if (!isConstant(parameter)) {
					parameter += index + "";
				}
				parameters.add(parameter.trim());
			}
			content = content.substring(0, content.length() - 1);
			content += ")";
			Literal literal = new Literal(content, truth, parameters);
			String symbol = content.substring(0, content.indexOf("(")).trim();
			if (truth) {
				if (!positive.containsKey(symbol)) {
					positive.put(symbol, new ArrayList<Literal>());
				}
				List<Literal> list = positive.get(symbol);
				if (!list.contains(literal)) {
					list.add(literal);
				}
			} else {
				if (!negative.containsKey(symbol)) {
					negative.put(symbol, new ArrayList<Literal>());
				}
				List<Literal> list = negative.get(symbol);
				if (!list.contains(literal)) {
					list.add(literal);
				}
			}
		} else {
			String sentenceContent = "";
			List<Literal> allLiterals = new ArrayList<Literal>();
			for (int i = 0; i < literals.length; i++) {
				String origin = literals[i].trim();
				String content = origin.substring(0, origin.indexOf("(") + 1).trim();
				Boolean truth;
				if (content.contains("~")) {
					truth = false;
					content = content.substring(1).trim();
				} else {
					truth = true;
				}
				List<String> parameters = new ArrayList<String>();

				String parameterContent = origin.substring(origin.indexOf("(") + 1, origin.indexOf(")")).trim();
				String[] parametersTmp = parameterContent.split(",");
				for (String parameter : parametersTmp) {
					parameter = parameter.trim();
					content += parameter + ",";
					if (!isConstant(parameter)) {
						parameter += index + "";
					}
					parameters.add(parameter.trim());
				}
				content = content.substring(0, content.length() - 1);
				content += ")";
				Literal literal = new Literal(content, truth, parameters);
				allLiterals.add(literal);
				if (literal.truth) {
					sentenceContent += literal.content + "|";
				} else {
					sentenceContent += "~" + literal.content + "|";
				}
			} 
			if (sentenceContent.length() > 0) {
				sentenceContent = sentenceContent.substring(0, sentenceContent.length() - 1);
			}
			Sentence sentence = new Sentence(sentenceContent, allLiterals);
			for (Literal item : allLiterals) {
				String symbol = item.content.substring(0, item.content.indexOf("(")).trim();
				if (item.truth) {
					if (!conclusion.containsKey(symbol)) {
						conclusion.put(symbol, new ArrayList<Sentence>());
					}
					List<Sentence> list = conclusion.get(symbol);
					if (!list.contains(sentence)) {
						list.add(sentence);
					}
				} else {
					if (!premise.containsKey(symbol)) {
						premise.put(symbol, new ArrayList<Sentence>());
					}
					List<Sentence> list = premise.get(symbol);
					if (!list.contains(sentence)) {
						list.add(sentence);
					}
				}
			}
		}
	}
	
	private static int resolutionHelper(Set<String> visited) {
		long startTime = System.currentTimeMillis();
		while (!allSentences.isEmpty()) {
			
			int size = allSentences.size();
			List<Sentence> newList = new ArrayList<Sentence>();
			for (int i = 0; i < size - 1; i++) {
				Sentence first = allSentences.get(i);
				for (int j = i + 1; j < size; j++) {
					Sentence second = allSentences.get(j);
					if (first.combined.contains(second.id) || second.combined.contains(first.id)) {
						continue;
					} else {
						first.combined.add(second.id);
						second.combined.add(first.id);
						for (Literal itemFirst : first.literals) {
							for (Literal itemSecond : second.literals) {
								Map<String, String> translate = null;
								if ((translate = isResolvable(itemFirst, itemSecond)) != null) {
									Sentence newSentence = createSentence(first, second, itemFirst, itemSecond, translate);
//									List<Sentence> list = createSentence(first, second, itemFirst, itemSecond, translate);
//									System.out.println("first: " + first.content + ", second: " + second.content);
//									for (Map.Entry<String, String> entry : translate.entrySet()) {
//										System.out.println(entry.getKey() + ":" + entry.getValue());
//									}
//									for (Sentence newSentence : list) {
//										System.out.println(newSentence.content);
										if (visited.contains(newSentence.content)) {
											continue;
										} else {
											if (existConflict(visited, newSentence) || newSentence.content.equals("")) {
												return -1;
											} else if (existSupport(visited, newSentence)) {
												return 1;
											} else {
												long endTime = System.currentTimeMillis();
												if (endTime - startTime > 10000) {
													return -2;
												} else {
													visited.add(newSentence.content);
													newList.add(newSentence);
												}
											}
										}
//									}
								} else {
									continue;
								}
							}
						}
					}
				}
			}
			if (newList.size() == 0) {
				break;
			} else {
				newList.addAll(allSentences);
				allSentences = newList;
			}
		}
		return 0;
	}
	
	private static boolean existConflict(Set<String> visited, Sentence sentence) {
		if (sentence.literals.size() != 1) {
			return false;
		}
		Literal item = sentence.literals.get(0);
		for (String str : visited) {
			int index = str.trim().indexOf("|");
			if (index == -1) {
				String content = str.trim().substring(0, str.trim().indexOf("(") + 1).trim();
				Boolean truth;
				if (content.contains("~")) {
					truth = false;
					content = content.substring(1).trim();
				} else {
					truth = true;
				}
				List<String> parameters = new ArrayList<String>();
				String parameterContent = str.trim().substring(str.trim().indexOf("(") + 1, str.trim().indexOf(")")).trim();
				String[] parametersTmp = parameterContent.split(",");
				for (String parameter : parametersTmp) {
					parameter = parameter.trim();
					content += parameter + ",";
					parameters.add(parameter.trim());
				}
				content = content.substring(0, content.length() - 1);
				content += ")";
				Literal literal = new Literal(content, truth, parameters);
				if (isResolvable(item, literal) != null) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean existSupport(Set<String> visited, Sentence sentence) {
		if (sentence.literals.size() != 1) {
			return false;
		}
		Literal item = sentence.literals.get(0);
		for (String str : visited) {
			String[] literals = str.trim().split("\\|");
			if (literals.length == 1) {
				String content = literals[0].trim().substring(0, literals[0].trim().indexOf("(") + 1).trim();
				Boolean truth;
				if (content.contains("~")) {
					truth = false;
					content = content.substring(1).trim();
				} else {
					truth = true;
				}
				List<String> parameters = new ArrayList<String>();
				String parameterContent = literals[0].trim().substring(literals[0].trim().indexOf("(") + 1, literals[0].trim().indexOf(")")).trim();
				String[] parametersTmp = parameterContent.split(",");
				for (String parameter : parametersTmp) {
					parameter = parameter.trim();
					content += parameter + ",";
					parameters.add(parameter.trim());
				}
				content = content.substring(0, content.length() - 1);
				content += ")";
				Literal literal = new Literal(content, truth, parameters);
				Map<String, String> map = null;
				if ((map = isUnifiable(item, literal)) != null) {
					boolean isSupport = true;
					for (String para : item.parameters) {
						if (!isConstant(para) && map.containsKey(para)) {
							isSupport = false;
							break;
						}
					}
					if (isSupport) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	private static Map<String, String> isUnifiable(Literal candidate,
			Literal item) {
		String symbol_candidate = candidate.content.substring(0, candidate.content.indexOf("("));
		String symbol_item = item.content.substring(0, item.content.indexOf("("));
		if (!symbol_candidate.equals(symbol_item)) {
			return null;
		}
		if (candidate.truth != item.truth) {
			return null;
		}
		
		return isCompatible(candidate, item);
	}
	
	private static Sentence createSentence(Sentence query, Sentence sentence,
			Literal candidate, Literal item, Map<String, String> translate) {
		//System.out.println(query.content);
		//System.out.println(sentence.content);
		Sentence query_tmp = removeLiteral(query, candidate);
		Sentence sentence_tmp = removeLiteral(sentence, item);
		translateSentence(query_tmp, translate);
		translateSentence(sentence_tmp, translate);
		return mergeSentence(query_tmp, sentence_tmp);
	}

	private static Sentence removeLiteral(Sentence query, Literal candidate) {
		List<Literal> list = new ArrayList<Literal>();
		String sentenceContent = "";
		for (Literal item : query.literals) {
			if (item.equals(candidate)) {
				continue;
			} else {
				List<String> parameters = new ArrayList<String>();
				for (String para : item.parameters) {
					parameters.add(new String(para));
				}
				String content = new String(item.content);
				boolean truth = item.truth;
				Literal newLiteral = new Literal(content, truth, parameters);
				list.add(newLiteral);
				if (newLiteral.truth) {
					sentenceContent += newLiteral.content;
				} else {
					sentenceContent += "~" + newLiteral.content;
				}
				sentenceContent += "|";
			}
		}
		if (sentenceContent.length() != 0) {
			sentenceContent = sentenceContent.substring(0, sentenceContent.length() - 1);
		}
		return new Sentence(sentenceContent, list);
	}
	

	private static void translateSentence(Sentence newSentence,
			Map<String, String> translate) {
		String sentenceContent = "";
		for (int i = 0; i < newSentence.literals.size(); i++) {
			Literal item = newSentence.literals.get(i);
			String content = item.content.substring(0, item.content.indexOf("(")) + "(";
			for (int j = 0; j < item.parameters.size(); j++) {
				String para = item.parameters.get(j);
				if (translate.containsKey(para)) {
					String newPara = translate.get(para);
					item.parameters.set(j, newPara);
				}
				String originPara = item.parameters.get(j);
				if (isConstant(originPara)) {
					content += originPara + ",";
				} else {
					content += originPara.substring(0, 1) + ",";
				}
			}
			content = content.substring(0, content.length() - 1);
			content += ")";
			item.content = content;
			if (item.truth) {
				sentenceContent += content + "|";
			} else {
				sentenceContent += "~" + content + "|";
			}
		}
		if (sentenceContent.equals("")) {
			return;
		}
		sentenceContent = sentenceContent.substring(0, sentenceContent.length() - 1);
		newSentence.content = sentenceContent;
		//System.out.println("critical: " + sentenceContent);
		return;
	}

	private static Sentence mergeSentence(Sentence query_tmp,
			Sentence sentence_tmp) {
		
		List<Literal> literals = new ArrayList<Literal>();
		//System.out.println("first sentence: " + query_tmp.content);
		//System.out.println("second sentence: " + sentence_tmp.content);
		for (Literal item: query_tmp.literals) {
			if (literals.contains(item)) {
				continue;
			} else {
				literals.add(item);
			}
		}
		for (Literal item: sentence_tmp.literals) {
			if (literals.contains(item)) {
				continue;
			} else {
				literals.add(item);
			}
		}
		String content = "";
		for (Literal item : literals) {
			String itemContent = item.content;
			if (!item.truth) {
				itemContent = "~" + itemContent;
			}
			content += itemContent + "|";
		}
		if (!content.equals("")) {
			content = content.substring(0, content.length() - 1);
		}
		return new Sentence(content, literals);
	}
	
	private static List<List<Literal>> dealRedundent(List<Literal> literals) {
		// TODO Auto-generated method stub
		List<List<Literal>> results = new ArrayList<List<Literal>>();
		for (int i = 0; i < literals.size() - 1; i++) {
			for (int j = i + 1; j < literals.size(); j++) {
				Literal item1 = literals.get(i);
				Literal item2 = literals.get(j);
				Map<String, String> map = null;
				if ((map = isCompatible(item1, item2)) != null) {
					List<Literal> tmp = createLiterals(literals, item2, map);
					List<List<Literal>> tmpResult = dealRedundent(tmp);
					results.addAll(tmpResult);
				}
			}
		}
		if (results.size() == 0) {
			results.add(literals);
		}
		return results;
	}
	
	private static List<Literal> createLiterals(List<Literal> literals, Literal candidate,
			Map<String, String> map) {
		List<Literal> newLiterals = literalClone(literals);
		newLiterals.remove(candidate);
		translate(newLiterals, map);
		return newLiterals;
	}

	
	private static List<Literal> literalClone(List<Literal> literals) {
		// TODO Auto-generated method stub
		List<Literal> list = new ArrayList<Literal>();
		for (Literal item : literals) {
			List<String> parameters = new ArrayList<String>();
			for (String para : item.parameters) {
				parameters.add(new String(para));
			}
			String content = new String(item.content);
			boolean truth = item.truth;
			Literal newLiteral = new Literal(content, truth, parameters);
			list.add(newLiteral);
		}
		return list;
	}

	private static void translate(List<Literal> literals, Map<String, String> map) {
		for (Literal item : literals) {
			String content = item.content.substring(0, item.content.indexOf("(")) + "(";
			for (int j = 0; j < item.parameters.size(); j++) {
				String para = item.parameters.get(j);
				if (map.containsKey(para)) {
					String newPara = map.get(para);
					item.parameters.set(j, newPara);
				}
				content += item.parameters.get(j) + ",";
			}
			content = content.substring(0, content.length() - 1);
			content += ")";
			item.content = content;
		}
	}
	
	private static Map<String, String> isResolvable(Literal candidate,
			Literal item) {
		String symbol_candidate = candidate.content.substring(0, candidate.content.indexOf("("));
		String symbol_item = item.content.substring(0, item.content.indexOf("("));
		if (!symbol_candidate.equals(symbol_item)) {
			return null;
		}
		if (candidate.truth == item.truth) {
			return null;
		}
		
		return isCompatible(candidate, item);
	}

	private static Map<String, String> isCompatible(Literal a, Literal b) {
		// TODO Auto-generated method stub
		List<String> params_a = a.parameters;
		List<String> params_b = b.parameters;
		if (params_a.size() != params_b.size()) {
			return null;
		}
		return unify(params_a, params_b, 0, params_a.size() - 1, new HashMap<String, String>());
	}
	
	private static Map<String, String> unify(List<String> A, List<String> B, int start, int end,
											Map<String, String> map) {
		if (map == null) {
			if (test(A,B)) {
				System.out.println("1");
			}
			return null;
		}
		boolean hasDiff = false;
		for (int i = start; i <= end; i++) {
			if (!A.get(i).equals(B.get(i))) {
				hasDiff = true;
			}
		}
		if (!hasDiff) {
			if (test(A,B)) {
				System.out.println("2");
			}
			return map;
		}
		if (start < end) {
			List<String> first_A = new ArrayList<String>();
			List<String> first_B = new ArrayList<String>();
			first_A.add(new String(A.get(start)));
			first_B.add(new String(B.get(start)));
			if (test(A,B)) {
				System.out.println("3");
			}
			return unify(A, B, start + 1, end, unify(first_A, first_B, 0, 0, map));
		}
		if (start == end) {
			String str_a = A.get(start);
			String str_b = B.get(start);
			if (!isConstant(str_a)) {
				if (test(A,B)) {
					System.out.println("4");
				}
				return unifyVar(str_a, str_b, map);
			} else if (!isConstant(str_b)) {
				if (test(A,B)) {
					System.out.println("5");
				}
				return unifyVar(str_b, str_a, map);
			} else {
				if (test(A,B)) {
					System.out.println("6");
				}
				return null;
			}
 		}
		if (test(A,B)) {
			System.out.println("7");
		}
		return null;
	}
	
	private static Boolean test(List<String> A, List<String> B) {
		if (A.size() != B.size()) {
			return false;
		}
		if (A.size() != 2) {
			return false;
		}
		if (A.get(0).equals("x1") && A.get(1).equals("Alice")&& B.get(0).equals("John") && B.get(1).equals("Alice")) {
			return true;
		}
		return false;
	}
	private static Map<String, String> unifyVar(String var, String x, Map<String, String> map) {
		if (map.containsKey(var)) {
			String val = map.get(var);
			List<String> first_val = new ArrayList<String>();
			List<String> first_x = new ArrayList<String>();
			first_val.add(new String(val));
			first_x.add(new String(x));
			return unify(first_val, first_x, 0, 0, map);
		} else if (map.containsKey(x)) {
			String val = map.get(x);
			List<String> first_var = new ArrayList<String>();
			List<String> first_val = new ArrayList<String>();
			first_var.add(new String(var));
			first_val.add(new String(val));
			return unify(first_var, first_val, 0, 0, map);
		} else {
			map.put(var, x);
			return map;
		}
	}
	private static boolean isConstant(String str) {
		return Character.isUpperCase(str.charAt(0));
	}
	
	private static void outputFile(List<String> results) {
		try {
//			FileWriter fw = new FileWriter("D:\\output.txt");// Need to be changed to the line below
			FileWriter fw = new FileWriter("output.txt");//used on Unix
			try {
				for (int i = 0; i < results.size(); i++) {
					String result = results.get(i);
					if (i != results.size() - 1) {
						result += "\n";
					}
					fw.write(result);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			fw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
/****************************************TEST***************************************/
	private static void testAll( ) {
		for (int i = 0; i < testcase.length; i++) {
			String data = testcase[i];
			System.out.println("For String " + i + " : " + data);
			TreeNode root = build(data);
//			convertToCNF(root);
//			storeInMemory(root , i);
			Debug(root);//test
		}
	}
	
	private static void Debug(TreeNode root) {
		Queue<TreeNode> queue = new LinkedList<TreeNode>();
		queue.offer(root);
		int depth = 1;
		while (!queue.isEmpty()) {
			int size = queue.size();
			for (int i = 0; i < size; i++) {
				TreeNode cur = queue.poll();
				System.out.println("level" + depth + ", the content is: " + cur.content);
				if (cur.left != null) {
					queue.offer(cur.left);
				}
				if (cur.right != null) {
					queue.offer(cur.right);
				}
			}
			depth++;
		}
	}
	private static void checkParameter() {
		int countQ = 0;
		int countKB = 0;
		System.out.println("ALL QUERIES:");
		for (int i = 0; i < queryRaw.size(); i++) {
			System.out.println(queryRaw.get(i));
			countQ++;
		}
		System.out.println("Number of query: " + countQ);
		System.out.println("ALL KB:");
		for (int i = 0; i < KB.size(); i++) {
			System.out.println(KB.get(i));
			countKB++;
		}
		System.out.println("Number of KB: " + countKB);
	}
	
	private static void debugAllMap() {
		for (Map.Entry<String, List<Literal>> entry : positive.entrySet()) {
			String symbol = entry.getKey();
			List<Literal> literals = entry.getValue();
			System.out.println("For symbol: " + symbol + ", its positive literals are:");
			for (Literal item : literals) {
				System.out.println(item.content + ", " + item.truth);
				for (String para : item.parameters) {
					System.out.print(para + ", ");
				}
				System.out.println("");
			}
		}
		for (Map.Entry<String, List<Literal>> entry : negative.entrySet()) {
			String symbol = entry.getKey();
			List<Literal> literals = entry.getValue();
			System.out.println("For symbol: " + symbol + ", its negative literals are:");
			for (Literal item : literals) {
				System.out.println(item.content + ", " + item.truth);
				for (String para : item.parameters) {
					System.out.print(para + ", ");
				}
				System.out.println("");
			}
		}
		for (Map.Entry<String, List<Sentence>> entry : conclusion.entrySet()) {
			String symbol = entry.getKey();
			List<Sentence> sentences = entry.getValue();
			System.out.println("For symbol: " + symbol + ", its conclusion sentences are:");
			for (Sentence item : sentences) {
				System.out.println("ID: " + item.id + ", content: " + item.content);
				for (Literal liter : item.literals) {
					for (String para : liter.parameters) {
						System.out.print(para + ", ");
					}
					System.out.println("");
				}
			}
		}
		for (Map.Entry<String, List<Sentence>> entry : premise.entrySet()) {
			String symbol = entry.getKey();
			List<Sentence> sentences = entry.getValue();
			System.out.println("For symbol: " + symbol + ", its premise sentences are:");
			for (Sentence item : sentences) {
				System.out.println("ID: " + item.id + ", content: " + item.content);
				for (Literal liter : item.literals) {
					for (String para : liter.parameters) {
						System.out.print(para + ", ");
					}
					System.out.println("");
				}
			}
		}
	}
	
	private static void debugQueryNegated() {
		for (Literal item : query_negated) {
			System.out.println("negated query is " + item.content + ", " + item.truth);
		}
	}
	
	private static void testOutput() {
		outputFile(fakeData());
	}
	
	private static List<String> fakeData() {
		List<String> results = new ArrayList<String>();
		for (int i = 0; i < queryRaw.size(); i++) {
			results.add("TRUE");
		}
		return results;
	}
	
	private static void testRemoveLiteral() {
		List<String> parameters_a = new ArrayList<String>();
		parameters_a.add("x");
		Literal a = new Literal("A(x)", false, parameters_a);
		List<String> parameters_b = new ArrayList<String>();
		parameters_b.add("x");
		Literal b = new Literal("B(x)", true, parameters_b);
		List<Literal> list = new ArrayList<Literal>();
		list.add(a);
		list.add(b);
		String content = "~A(x)|B(x)";
		Sentence sentence = new Sentence(content, list);
		Sentence test = removeLiteral(sentence, b);
		Sentence test1 = removeLiteral(test, a);
		System.out.println(sentence.content);
		System.out.println(sentence.literals.size());
		System.out.println(test.content);
		System.out.println(test.literals.size());
		System.out.println(test1.content);
		System.out.println(test1.literals.size());
	}
	
	private static void testTranslate() {
		List<String> parameters_a = new ArrayList<String>();
		parameters_a.add("x");
		Literal a = new Literal("A(x)", false, parameters_a);
		List<String> parameters_b = new ArrayList<String>();
		parameters_b.add("y");
		Literal b = new Literal("B(y)", true, parameters_b);
		List<Literal> list = new ArrayList<Literal>();
		list.add(a);
		list.add(b);
		String content = "~A(x)|B(y)";
		Sentence sentence = new Sentence(content, list);
		Map<String, String> map = new HashMap<String, String>();
		map.put("x", "sb");
		map.put("y", "shini");
		translateSentence(sentence, map);
		System.out.println(sentence.content);
		for (Literal item : sentence.literals) {
			System.out.println(item.content);
		}
	}
}

