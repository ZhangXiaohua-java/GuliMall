package element.io.secskill;

import element.io.mall.common.to.SeckillSkuRelationTo;
import element.io.secskill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@SpringBootTest
public class SecSkillApplicationTests {


	@Resource
	private SecKillService service;

	@Test
	public void contextLoads() {
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		String start = dateTimeFormatter.format(dateTime);
		LocalDateTime next = LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.MAX);
		String end = dateTimeFormatter.format(next);
		System.out.println(start);
		System.out.println(end);
	}


	@Test
	public void testScan() {
		List<SeckillSkuRelationTo> reesult = service.queryCuurentSecKillProducts();
		log.info("最终结果{}", reesult);
	}

}
