package element.io.search.service.impl;

import com.alibaba.fastjson.JSON;
import element.io.mall.common.enumerations.ElasticSearchConstant;
import element.io.mall.common.to.SkuEsModelTo;
import element.io.search.service.IEsStorageService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-13
 */
@Service
@Slf4j
public class IEsStorageServiceImpl implements IEsStorageService {

	@Resource
	private RestHighLevelClient restHighLevelClient;


	@Override
	public boolean storageSkuInfo(List<SkuEsModelTo> skuEsModelTos) throws IOException {
		log.info("接收到的数据{}", skuEsModelTos);
		BulkRequest bulkRequest = new BulkRequest();
		for (SkuEsModelTo model : skuEsModelTos) {
			IndexRequest indexRequest = new IndexRequest(ElasticSearchConstant.PRODUCT_INDEX);
			indexRequest.id(model.getSkuId().toString());
			String s = JSON.toJSONString(model);
			indexRequest.source(s, XContentType.JSON);
			bulkRequest.add(indexRequest);
		}
		BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
		log.info("ES执行的结果{}", bulkResponse);
		return !bulkResponse.hasFailures() ? true : false;
	}


}
