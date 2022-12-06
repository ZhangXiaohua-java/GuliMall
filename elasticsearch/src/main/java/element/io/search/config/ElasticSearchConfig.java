package element.io.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 张晓华
 * @date 2022-11-10
 */
@Configuration
public class ElasticSearchConfig {

	@Bean
	public RestHighLevelClient restHighLevelClient() {
		RestHighLevelClient restHighLevelClient = null;
		RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost("192.168.1.9", 9200, "http"));
		restHighLevelClient = new RestHighLevelClient(restClientBuilder);
		return restHighLevelClient;
	}


}
