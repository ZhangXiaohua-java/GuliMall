package element.io.mall.member;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GulimailMemberApplicationTests {
	public static void main(String[] args) {
		encrypt();
	}

	public static void encrypt() {
		String rawText = "123456";
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		// $2a$10$iANEsQA1UjBvOeAdxxkN1.OqpqmplI/NzElmYUdSjtScWF6m1vV9y
		// $2a$10$zQXUFiOllSdewvgYSeGk/Odqs/dgrxlAleDLsqBVbDzhgDm2P1DXK
		//String str = passwordEncoder.encode(rawText);
		//System.out.println(str);
		//boolean matches = passwordEncoder.matches(rawText, "$2a$10$iANEsQA1UjBvOeAdxxkN1.OqpqmplI/NzElmYUdSjtScWF6m1vV9y");
		//System.out.println(matches);
		System.out.println(passwordEncoder.encode("zxh0309521"));
	}

}
