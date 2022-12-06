package element.io.mall.cart;

import java.util.HashSet;

public class GulimallCartApplicationTests {


	//public static void main(String[] args) {
	//	Stream<Integer> stream = IntStream.range(0, 101).boxed();
	//	int value = stream.reduce(0, (pre, curr) -> {
	//		System.out.println(pre);
	//		System.out.println(curr);
	//		System.out.println("--------");
	//		return pre += curr;
	//	}).intValue();
	//	System.out.println(value);
	//}

	public static void main(String[] args) {
		HashSet<Integer> a = new HashSet<>();
		a.add(1);
		a.add(2);
		HashSet<Integer> b = new HashSet<>();
		b.add(1);
		b.add(2);
		b.add(3);
		HashSet<Integer> c = new HashSet<>();
		c.addAll(a);
		c.removeAll(b);
		System.out.println(c);

	}


}
