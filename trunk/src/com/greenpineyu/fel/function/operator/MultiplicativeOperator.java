package com.greenpineyu.fel.function.operator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.greenpineyu.fel.common.NumberUtil;
import com.greenpineyu.fel.compile.FelMethod;
import com.greenpineyu.fel.context.FelContext;
import com.greenpineyu.fel.exception.EvalException;
import com.greenpineyu.fel.function.CommonFunction;
import com.greenpineyu.fel.parser.FelNode;
import com.greenpineyu.fel.parser.Stable;

/**
 * 包名				.script.function.operator
 * 类名				MultiplicativeOperator.java
 * 创建日期				Oct 26, 20102:47:15 PM
 * 作者				
 * 版权				
 */
public class MultiplicativeOperator extends CommonFunction implements Stable{

	private String operator;
	
	private MultiplicativeOperator(String operator) {
		this.operator = operator;
	}
	
	public static final String MUL_STR = "*";
	
	public static final String DIV_STR = "/";
	
	public static final String MOD_STR = "%";
	
	public static final MultiplicativeOperator MUL;
	
	public static final MultiplicativeOperator DIV;
	
	public static final MultiplicativeOperator MOD;
	
	static {
		MUL = new MultiplicativeOperator(MUL_STR);
		DIV = new MultiplicativeOperator(DIV_STR);
		MOD = new MultiplicativeOperator(MOD_STR);
	}
	
	public Object call(Object[] arguments) {
		if(arguments != null && arguments.length == 2){
			Object left = arguments[0];
			Object right = arguments[1];
			if(this == MUL){
				return multiply(left, right);
			}else if(this == DIV){
				return divide(left, right);
			}else if(this == MOD){
				return mod(left, right);
			}
		}
		throw new NullPointerException("传入参数数组为空或者参数个数不正确!");
	}
	
	@Override
	public Object call(FelNode node, FelContext context) {
		List<FelNode> children = node.getChildren();
		if (children.size() == 2) {
			FelNode left = children.get(0);
			Object leftValue = left.eval(context);
			FelNode right = children.get(1);
			Object rightValue = right.eval(context);
			if (leftValue instanceof Number && rightValue instanceof Number) {
				double l = NumberUtil.toDouble(leftValue);
				double r = NumberUtil.toDouble(rightValue);
				if(this == MUL){
					return NumberUtil.parseNumber(l * r);
				}
				if(this == DIV){
					return NumberUtil.parseNumber(l / r);
				}
				if(this == MOD){
					return NumberUtil.parseNumber(l % r);
				}
				throw new EvalException("执行"+this.operator+"出错，未知的操作符");
			}
			throw new EvalException("执行"+this.operator+"出错，参数必须是数值型");
		}
		return super.call(node, context);
	}

	/**
	 * 乘法
	 * @param left
	 * @param right
	 * @return
	 */
	public static Object multiply(Object left, Object right) {
        if (left == null || right == null) {
			throw new NullPointerException("调用multiply()方法出错！,原因：当前参数为空");
        }
        try {
        	if (left instanceof Object[]){
				left = AdditiveOperator.calArray(left);
			}
			if (right instanceof Object[]){
				right = AdditiveOperator.calArray(right);
			}
			if (left.equals("∞") || right.equals("∞"))
				return "∞";
			
			if (NumberUtil.isFloatingPointNumber(left) || NumberUtil.isFloatingPointNumber(right)) {
			    double l = NumberUtil.toDouble(left);
			    double r = NumberUtil.toDouble(right);
			    return new Double(l * r);
			}
			if (left instanceof BigInteger && right instanceof BigInteger) {
			    BigInteger l = NumberUtil.toBigInteger(left);
			    BigInteger r = NumberUtil.toBigInteger(right);
			    return l.multiply(r);
			}
			if (left instanceof BigDecimal || right instanceof BigDecimal) {
			    BigDecimal l = NumberUtil.toBigDecimal(left);
			    BigDecimal r = NumberUtil.toBigDecimal(right);
			    return l.multiply(r);
			}
			BigInteger l = NumberUtil.toBigInteger(left);
			BigInteger r = NumberUtil.toBigInteger(right);
			BigInteger result = l.multiply(r);
			return NumberUtil.narrowBigInteger(left, right, result);
		} catch (Exception e) {
			throw new NumberFormatException("调用multiply()方法出错！,原因：解析参数对象出错！");
		}
    }

	/**
	 * 除法
	 * @param left
	 * @param right
	 * @return
	 */
	public static Object divide(Object left, Object right) {
        if (left == null || right == null) {
			throw new NullPointerException("调用divide()方法出错！,原因：当前参数为空");
        }
        try {
        	if (left instanceof Object[]){
				left = AdditiveOperator.calArray(left);
			}
			if (right instanceof Object[]){
				right = AdditiveOperator.calArray(right);
			}
			if (left.equals("∞") || right.equals("∞")) {
				return "∞";
			}
        	
			if (NumberUtil.isFloatingPointNumber(left) || NumberUtil.isFloatingPointNumber(right)) {
			    double l = NumberUtil.toDouble(left);
			    double r = NumberUtil.toDouble(right);
			    if (r == 0.0) {
//			        throw new ArithmeticException("/");
					return "∞"; // 被除数是零时返回无穷大
			    }
			    return new Double(l / r);
			}
			if (left instanceof Integer && right instanceof Integer || left instanceof Long && right instanceof Long) {
				Number r = (Number) right;
				if (r.intValue() == 0) {
					return "∞";
				}
				Number l = (Number) left;
				return new Double(l.doubleValue() / r.intValue());
			}
			if (left instanceof BigInteger && right instanceof BigInteger) {
			    BigInteger l = NumberUtil.toBigInteger(left);
			    BigInteger r = NumberUtil.toBigInteger(right);
			    return l.divide(r);
			}
			//默认使用浮点运算,使用整型运算会导致略去小数位的问题
			//			if (left instanceof BigDecimal || right instanceof BigDecimal) {
			    BigDecimal l = NumberUtil.toBigDecimal(left);
			    BigDecimal r = NumberUtil.toBigDecimal(right);
			    BigDecimal d = l.divide(r,8,BigDecimal.ROUND_DOWN);
			    return d;
			//			}
			//			BigInteger l = NumberUtil.toBigInteger(left);
			//			BigInteger r = NumberUtil.toBigInteger(right);

			//			BigInteger result = l.divide(r);
			//			return NumberUtil.narrowBigInteger(left, right, result);
		} catch (Exception e) {
			throw new EvalException(left + "/" + right + "运算出错", e);
		}
    }

	/**
	 * 除于
	 * @param left
	 * @param right
	 * @return
	 */
	private Object mod(Object left, Object right) {
        if (left == null || right == null) {
			throw new NullPointerException("调用mod()方法出错！,原因：当前参数为空");
        }
        try {
        	if (left instanceof Object[]){
				left = AdditiveOperator.calArray(left);
			}
			if (right instanceof Object[]){
				right = AdditiveOperator.calArray(right);
			}
			if (left.equals("∞") || right.equals("∞"))
				return "∞";
			
			if (NumberUtil.isFloatingPointNumber(left) || NumberUtil.isFloatingPointNumber(right)) {
			    double l = NumberUtil.toDouble(left);
			    double r = NumberUtil.toDouble(right);
			    if (r == 0.0) {
			        throw new ArithmeticException("%");
			    }
			    return new Double(l % r);
			}
			if (left instanceof BigInteger && right instanceof BigInteger) {
			    BigInteger l = NumberUtil.toBigInteger(left);
			    BigInteger r = NumberUtil.toBigInteger(right);
			    return l.mod(r);
			}
			if (left instanceof BigDecimal || right instanceof BigDecimal) {
			    BigDecimal l = NumberUtil.toBigDecimal(left);
			    BigDecimal r = NumberUtil.toBigDecimal(right);
			    BigDecimal remainder = l.subtract(l.divide(r,BigDecimal.ROUND_DOWN).multiply(r));
			    return remainder;
			}
			BigInteger l = NumberUtil.toBigInteger(left);
			BigInteger r = NumberUtil.toBigInteger(right);
			BigInteger result = l.mod(r);
			return NumberUtil.narrowBigInteger(left, right, result);
		} catch (Exception e) {
			throw new NumberFormatException("调用mod()方法出错！,原因：解析参数对象出错！");
		}
    }

	public String getName() {
		return this.operator;
	}

	public FelMethod toMethod(FelNode node, FelContext ctx) {
		String code = "";
		FelNode left = node.getChildren().get(0);
		FelNode right = node.getChildren().get(1);
		code = "("+left.toMethod(ctx).source(ctx, left)+")"+this.operator+"("+right.toMethod(ctx).source(ctx, right)+")";
		FelMethod m = new FelMethod(Number.class, code);
		return m;
	}

	public boolean stable() {
		return true;
	}

}
