// Generated from LexerParser.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LexerParserParser}.
 */
public interface LexerParserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LexerParserParser#r}.
	 * @param ctx the parse tree
	 */
	void enterR(LexerParserParser.RContext ctx);
	/**
	 * Exit a parse tree produced by {@link LexerParserParser#r}.
	 * @param ctx the parse tree
	 */
	void exitR(LexerParserParser.RContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nested}
	 * labeled alternative in {@link LexerParserParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterNested(LexerParserParser.NestedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nested}
	 * labeled alternative in {@link LexerParserParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitNested(LexerParserParser.NestedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code negation}
	 * labeled alternative in {@link LexerParserParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterNegation(LexerParserParser.NegationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code negation}
	 * labeled alternative in {@link LexerParserParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitNegation(LexerParserParser.NegationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code pred}
	 * labeled alternative in {@link LexerParserParser#formula}.
	 * @param ctx the parse tree
	 */
	void enterPred(LexerParserParser.PredContext ctx);
	/**
	 * Exit a parse tree produced by the {@code pred}
	 * labeled alternative in {@link LexerParserParser#formula}.
	 * @param ctx the parse tree
	 */
	void exitPred(LexerParserParser.PredContext ctx);
	/**
	 * Enter a parse tree produced by {@link LexerParserParser#op}.
	 * @param ctx the parse tree
	 */
	void enterOp(LexerParserParser.OpContext ctx);
	/**
	 * Exit a parse tree produced by {@link LexerParserParser#op}.
	 * @param ctx the parse tree
	 */
	void exitOp(LexerParserParser.OpContext ctx);
	/**
	 * Enter a parse tree produced by {@link LexerParserParser#predicate}.
	 * @param ctx the parse tree
	 */
	void enterPredicate(LexerParserParser.PredicateContext ctx);
	/**
	 * Exit a parse tree produced by {@link LexerParserParser#predicate}.
	 * @param ctx the parse tree
	 */
	void exitPredicate(LexerParserParser.PredicateContext ctx);
	/**
	 * Enter a parse tree produced by {@link LexerParserParser#term}.
	 * @param ctx the parse tree
	 */
	void enterTerm(LexerParserParser.TermContext ctx);
	/**
	 * Exit a parse tree produced by {@link LexerParserParser#term}.
	 * @param ctx the parse tree
	 */
	void exitTerm(LexerParserParser.TermContext ctx);
}