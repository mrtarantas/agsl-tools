package com.mrtarantas.agsl.language

import com.intellij.formatting.*
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.formatter.common.AbstractBlock
import com.intellij.psi.tree.TokenSet
import com.mrtarantas.agsl.language.generated.psi.AgslTypes

class AgslFormattingModelBuilder : FormattingModelBuilder {
	override fun createModel(formattingContext: FormattingContext): FormattingModel {
		val settings = formattingContext.codeStyleSettings
		val root = AgslBlock(formattingContext.node, null, null, spacingBuilder(settings))
		return FormattingModelProvider.createFormattingModelForPsiFile(
			formattingContext.containingFile, root, settings,
		)
	}

	private fun spacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
		val addOps = TokenSet.create(AgslTypes.PLUS, AgslTypes.MINUS)
		val mulOps = TokenSet.create(AgslTypes.STAR, AgslTypes.SLASH)
		val relOps = TokenSet.create(AgslTypes.LT, AgslTypes.GT, AgslTypes.LE, AgslTypes.GE)
		val eqOps = TokenSet.create(AgslTypes.EQEQ, AgslTypes.NEQ)
		val assignOps = TokenSet.create(
			AgslTypes.EQ, AgslTypes.PLUS_EQ, AgslTypes.MINUS_EQ, AgslTypes.STAR_EQ, AgslTypes.SLASH_EQ,
		)
		return SpacingBuilder(settings, AgslLanguage)
			.aroundInside(addOps, AgslTypes.ADDITIVE_EXPR).spaces(1)
			.aroundInside(mulOps, AgslTypes.MULTIPLICATIVE_EXPR).spaces(1)
			.aroundInside(relOps, AgslTypes.RELATIONAL_EXPR).spaces(1)
			.aroundInside(eqOps, AgslTypes.EQUALITY_EXPR).spaces(1)
			.aroundInside(TokenSet.create(AgslTypes.ANDAND), AgslTypes.LOGICAL_AND_EXPR).spaces(1)
			.aroundInside(TokenSet.create(AgslTypes.OROR), AgslTypes.LOGICAL_OR_EXPR).spaces(1)
			.aroundInside(TokenSet.create(AgslTypes.XORXOR), AgslTypes.LOGICAL_XOR_EXPR).spaces(1)
			.aroundInside(assignOps, AgslTypes.ASSIGNMENT_EXPR).spaces(1)
			.aroundInside(TokenSet.create(AgslTypes.EQ), AgslTypes.INIT_DECLARATOR).spaces(1)
			.before(AgslTypes.COMMA).spaces(0)
			.after(AgslTypes.COMMA).spaces(1)
			.before(AgslTypes.SEMI).spaces(0)
			.after(AgslTypes.LPAREN).spaces(0)
			.before(AgslTypes.RPAREN).spaces(0)
			.between(AgslTypes.KW_IF, AgslTypes.LPAREN).spaces(1)
			.between(AgslTypes.KW_FOR, AgslTypes.LPAREN).spaces(1)
			.between(AgslTypes.KW_WHILE, AgslTypes.LPAREN).spaces(1)
			.after(AgslTypes.KW_RETURN).spaces(1)
			.before(AgslTypes.COMPOUND_STMT).spaces(1)
			.before(AgslTypes.LBRACE).spaces(1)
			.after(AgslTypes.KW_UNIFORM).spaces(1)
			.after(AgslTypes.KW_CONST).spaces(1)
			.between(AgslTypes.KW_IN, AgslTypes.TYPE).spaces(1)
			.between(AgslTypes.KW_OUT, AgslTypes.TYPE).spaces(1)
			.between(AgslTypes.KW_INOUT, AgslTypes.TYPE).spaces(1)
			.between(AgslTypes.TYPE, AgslTypes.VAR_LIST).spaces(1)
			.between(AgslTypes.TYPE, AgslTypes.INIT_DECLARATOR_LIST).spaces(1)
			.between(AgslTypes.TYPE, AgslTypes.IDENT).spaces(1)
			.betweenInside(AgslTypes.IDENT, AgslTypes.LPAREN, AgslTypes.FUNC_DEF).spaces(0)
	}
}

private class AgslBlock(
	node: ASTNode,
	wrap: Wrap?,
	alignment: Alignment?,
	private val spacingBuilder: SpacingBuilder,
) : AbstractBlock(node, wrap, alignment) {

	override fun buildChildren(): MutableList<Block> {
		val children = mutableListOf<Block>()
		var child = node.firstChildNode
		while (child != null) {
			if (child.elementType != TokenType.WHITE_SPACE && child.textRange.length > 0)
				children.add(AgslBlock(child, null, null, spacingBuilder))
			child = child.treeNext
		}
		return children
	}

	override fun getIndent(): Indent {
		val parent = node.treeParent ?: return Indent.getNoneIndent()
		val type = node.elementType
		if (parent.elementType == AgslTypes.COMPOUND_STMT && type != AgslTypes.LBRACE && type != AgslTypes.RBRACE)
			return Indent.getNormalIndent()
		if (parent.treeParent == null)
			return Indent.getAbsoluteNoneIndent()
		return Indent.getNoneIndent()
	}

	override fun getSpacing(child1: Block?, child2: Block): Spacing? = spacingBuilder.getSpacing(this, child1, child2)

	override fun isLeaf(): Boolean = node.firstChildNode == null

	override fun getChildAttributes(newChildIndex: Int): ChildAttributes {
		val indent = when (node.elementType) {
			AgslTypes.COMPOUND_STMT -> Indent.getNormalIndent()
			else -> Indent.getNoneIndent()
		}
		return ChildAttributes(indent, null)
	}
}
