package element.io.search;

import com.alibaba.fastjson.JSON;
import element.io.search.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchApplicationTests {

	@Resource
	private RestHighLevelClient restHighLevelClient;

	@Test
	public void any() {
		System.out.println(restHighLevelClient);
	}

	// 创建索引
	@Test
	public void basicQuery() throws IOException {
		CreateIndexRequest indexRequest = new CreateIndexRequest("java");
		CreateIndexResponse response = restHighLevelClient.indices().create(indexRequest, RequestOptions.DEFAULT);
		String index = response.index();
		boolean acknowledged = response.isAcknowledged();
		log.info("创建的索引名{}, 是否创建成功{}", index, acknowledged);
	}

	// 查看索引
	@Test
	public void queryIndices() throws IOException {
		GetIndexRequest getIndexRequest = new GetIndexRequest("java");
		GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(getIndexRequest, RequestOptions.DEFAULT);
		String[] indices = getIndexResponse.getIndices();
		for (String index : indices) {
			log.info("遍历到的index{}", index);
		}
		Map<String, MappingMetaData> mappings = getIndexResponse.getMappings();
		log.info("获取到的mappings{}", mappings);
		Map<String, List<AliasMetaData>> aliases = getIndexResponse.getAliases();
		log.info("获取到的alias{}", aliases);
	}

	// 查看索引的索引
	@Test
	public void getAllIndices() throws IOException {
		GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(new GetIndexRequest("*"), RequestOptions.DEFAULT);
		String[] indices = getIndexResponse.getIndices();
		for (String index : indices) {
			log.info("获取到的索引 {}", index);
		}
	}

	@Test
	public void deleteIndex() throws IOException {
		DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("java");
		AcknowledgedResponse response = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
		boolean acknowledged = response.isAcknowledged();
		log.info(acknowledged ? "删除成功" : "删除失败");
	}

	// 创建文档
	@Test
	public void createDoc() throws IOException {
		IndexRequest indexRequest = new IndexRequest();
		// 设置索引名
		indexRequest.index("java").id("1");
		User user = User.builder().id(1L)
				.name("张晓华")
				.age(20)
				.gender("男")
				.address("河南省郑州市HUEL").build();
		// 转换为json处理
		String userJson = JSON.toJSONString(user);
		// 设置数据并指定数据的类型
		indexRequest.source(userJson, XContentType.JSON);
		IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
		RestStatus status = indexResponse.status();
		int flag = status.getStatus();
		log.info("响应的状态码{}", flag);
		log.info("id{},version{},seq_no{}", indexResponse.getId(), indexResponse.getVersion(), indexResponse.getSeqNo());
		log.info("响应信息{}", indexResponse);
	}

	// 更新文档
	@Test
	public void updateDoc() throws IOException {
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index("java").id("1");
		User user = User.builder().email("javago0309@163.com").build();
		String data = JSON.toJSONString(user);
		updateRequest.doc(data, XContentType.JSON);
		UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
		int status = updateResponse.status().getStatus();
		log.info("响应的状态码{}", status);
		long version = updateResponse.getVersion();
		long seqNo = updateResponse.getSeqNo();
		log.info("文档的版本号{},文档的序列号{}", version, seqNo);
	}

	// 查询文档
	@Test
	public void queryDoc() throws IOException {
		GetRequest getRequest = new GetRequest();
		getRequest.index("java").id("1");
		GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
		long version = getResponse.getVersion();
		long seqNo = getResponse.getSeqNo();
		Map<String, DocumentField> fields = getResponse.getFields();
		log.info("查询到的数据的version {}, seq_no{}", version, seqNo);
		log.info("fields{}", fields);
		String source = getResponse.getSourceAsString();
		log.info("json格式的数据{}", source);
		User user = JSON.parseObject(source, User.class);
		log.info("查询出的对象信息{}", user);
	}

	// 删除文档
	@Test
	public void deleteDoc() throws IOException {
		DeleteRequest deleteRequest = new DeleteRequest().index("java").id("2");
		DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
		int status = deleteResponse.status().getStatus();
		log.info("响应状态码{}", status);
		log.info("完整的响应信息{}", deleteResponse);
	}

	@Test
	public void bulkOperation() throws IOException {
		BulkRequest bulkRequest = new BulkRequest();
		User user = User.builder().id(3L).age(18).name("test").address("河南郑州").gender("未知").email("test@qq.com").build();
		IndexRequest indexRequest = new IndexRequest().index("java").id("3").source(JSON.toJSONString(user), XContentType.JSON);
		bulkRequest.add(indexRequest);
		DeleteRequest deleteRequest = new DeleteRequest().index("java").id("2");
		bulkRequest.add(deleteRequest);
		BulkResponse bulkItemResponses = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		for (BulkItemResponse item : bulkItemResponses.getItems()) {
			log.info("批量操作的响应信息{}", item);
		}
		TimeValue timeValue = bulkItemResponses.getTook();
		long millis = timeValue.getMillis();
		log.info("执行用时{}毫秒", millis);
	}

	
}
