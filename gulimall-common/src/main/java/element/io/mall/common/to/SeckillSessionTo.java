package element.io.mall.common.to;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 秒杀活动场次
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:31:53
 */
@Data
public class SeckillSessionTo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;
	/**
	 * 场次名称
	 */
	private String name;
	/**
	 * 每日开始时间
	 */
	private Date startTime;
	/**
	 * 每日结束时间
	 */
	private Date endTime;
	/**
	 * 启用状态
	 */
	private Integer status;
	/**
	 * 创建时间
	 */
	private Date createTime;

	private List<SeckillSkuRelationTo> relations;

}
