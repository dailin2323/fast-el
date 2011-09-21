package com.greenpineyu.fel.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;
import org.apache.commons.lang3.ObjectUtils.Null;

import com.greenpineyu.fel.compile.FelMethod;
import com.greenpineyu.fel.compile.SourceBuilder;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.interpreter.Interpreter;

public abstract class AbstFelNode extends CommonTree implements FelNode, Interpreter {

	/**
	 * 解析器,用于解析节点的值
	 */
	protected Interpreter interpreter;

	/**
	 * 默认的解析器
	 */
	protected Interpreter defaultInter;

	
	protected SourceBuilder builder;

	@Override
	@SuppressWarnings("unchecked")
	public List<FelNode> getChildren() {
		return this.children;
	}

	/**
	 * 点位节点
	 */
	public static final AbstFelNode NULL_NODE = new AbstFelNode() {
		@Override
		public boolean isNil() {
			return false;
		};

		@Override
		public Object interpret(FelContext context, FelNode node) {
			return null;
		};
		
		@Override
		public String toString() {return "NULL_NODE";}

		@Override
		public SourceBuilder toMethod(FelContext ctx) {
			throw new UnsupportedOperationException("占位节点");
		}

	};
	
	public static final AbstFelNode NULL = new AbstFelNode() {
		@Override
		public boolean isNil() {
			return false;
		};
		
		@Override
		public Object interpret(FelContext context, FelNode node) {
			return null;
		};
		
		@Override
		public String getText() {
			return "null";
		};
		
		
		@Override
		public String toString() {return "NULL_NODE";}
		
		@Override
		public SourceBuilder toMethod(FelContext ctx) {
			return new FelMethod(Null.class, "null");
		}
		
	};
	
	public static final FelNode TRUE_NODE = new AbstFelNode(){
		@Override
		public boolean isNil() {
			return false;
		};
		
		@Override
		public Object interpret(FelContext context, FelNode node) {
			return Boolean.TRUE;
		}

		@Override
		public SourceBuilder toMethod(FelContext ctx) {
			return new FelMethod(Boolean.class, "true");
		}


	};


	public AbstFelNode(Token token) {
		super(token);
	}

	public AbstFelNode() {

	}

	{
		// 解释器设置成this
		this.defaultInter = this;
		resetInterpreter();
		// 源码构建器设置成this
//		resetSourceBuilder();
	}


	@SuppressWarnings("unchecked")
	public AbstFelNode(CommonTree node) {
		super(node);
		if (node.getChildren() != null) {
			this.children = new ArrayList<FelNode>(node.getChildren());
			for (int i = 0; i < this.children.size(); i++) {
				Tree object = (Tree) this.children.get(i);
				object.setParent(this);
			}
		}
	}


	@Override
	public String toString() {
		return this.getText();
	}




	public void setChild(int index, FelNode node) {
		if (node instanceof Tree) {
			super.setChild(index, node);
		} else {
			throw new IllegalArgumentException("类型必须是" + Tree.class);
		}

	}



	@Override
	public Object eval(FelContext context) {
		return interpreter.interpret(context, this);
		//		if (cached) {
		//			return cacheValue;
		//		}
		//		Object eval = interpreter.interpret(context, this);
		//		if (fixed) {
		//			cacheValue = eval;
		//			cached = true;
		//		}
		//		return eval;
	}

	//	abstract public Object evalWithoutCache(FelContext context);


	public static List<FelNode> getNodes(FelNode node) {
		List<FelNode> returnMe = new ArrayList<FelNode>();
		getNodes(node, returnMe);
		return returnMe;
	}

	public static void getNodes(FelNode node, List<FelNode> returnMe) {
		if (node != null) {
			returnMe.add(node);
			List<FelNode> nodeChildren = node.getChildren();
			if (nodeChildren != null) {
				for (Iterator<FelNode> iterator = nodeChildren.iterator(); iterator.hasNext();) {
					try {
						FelNode child = iterator.next();
						getNodes(child, returnMe);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}


	@Override
	public Interpreter getInterpreter() {
		return this.interpreter;
	}

	@Override
	public void setInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	@Override
	public void resetInterpreter() {
		this.interpreter = this.defaultInter;
	}

	/**
	 * 是否默认的解释器
	 * 
	 * @return
	 */
	@Override
	public boolean isDefaultInterpreter(){
		return this.interpreter == this.defaultInter;
	}
	
	

	@Override
	public Object interpret(FelContext context, FelNode node) {
		throw new UnsupportedOperationException("还没有实现[2011-1-13]");
	}
	
	@Override
	public SourceBuilder toMethod(FelContext ctx){
		return this.builder;
	}
	
	@Override
	public void setSourcebuilder(SourceBuilder builder) {
		this.builder = builder;
	}
	
	@Override
	public boolean stable() {
		return false;
	}

	public boolean isChildrenStable() {
		if(this.children!=null){
			// 子节点有一个不是稳定的，就返回false
			for (int i = 0; i < children.size(); i++) {
				FelNode child = (FelNode) children.get(i);
				if(!child.stable()){
					return false;
				}
			}
		}
		return true;
	}
	
	
//	public void resetSourceBuilder(){
//		this.builder = this;
//	}
	

}