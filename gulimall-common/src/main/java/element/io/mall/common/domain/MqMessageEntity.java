package element.io.mall.common.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 19:05:36
 */
@Data
public class MqMessageEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String JSON = "json";

	/**
	 *
	 */
	private String messageId;
	/**
	 *
	 */
	private String content;
	/**
	 *
	 */
	private String toExchane;
	/**
	 *
	 */
	private String routingKey;
	/**
	 *
	 */
	private String classType;
	/**
	 * 0-新建 1-已发送 2-错误抵达 3-已抵达
	 */
	private Integer messageStatus;
	/**
	 *
	 */
	private Date createTime;
	/**
	 *
	 */
	private Date updateTime;

}
