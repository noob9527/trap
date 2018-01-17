// Emulated extensible enum using an interface - Basic implementation - Page 165
package cn.staynoob.trap.java.effectivejava.chapter06.item34;

public enum BasicOperation implements Operation {
	PLUS("+") {
		public double apply(double x, double y) {
			return x + y;
		}
	},
	MINUS("-") {
		public double apply(double x, double y) {
			return x - y;
		}
	},
	TIMES("*") {
		public double apply(double x, double y) {
			return x * y;
		}
	},
	DIVIDE("/") {
		public double apply(double x, double y) {
			return x / y;
		}
	};
	private final String symbol;

	BasicOperation(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public String toString() {
		return symbol;
	}
}
