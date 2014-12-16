package message.jdbc.utils.helper;

import message.jdbc.convert.ConvertBean;
import message.jdbc.convert.Convert;
import message.jdbc.convert.ConvertGetter;
import message.jdbc.key.IDGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 不同数据库处理的辅助类
 *
 * @author sunhao(sunhao.java@gmail.com)
 * @version V1.0, 2012-4-10 上午01:23:20
 */
public abstract class SqlHelper {
    protected IDGenerator idGenerator;
    private Map<Class<?>, Convert> realConverts = new HashMap<Class<?>, Convert>();

    /**
     * 设置long型值
     *
     * @param ps
     * @param index
     * @param value
     * @throws java.sql.SQLException
     */
    public void setLongStringValue(PreparedStatement ps, int index, String value) throws SQLException {
        ps.setString(index, value);
    }

    /**
     * 设置clob型值
     *
     * @param ps
     * @param index
     * @param value
     * @throws java.sql.SQLException
     */
    public void setClobStringVlaue(PreparedStatement ps, int index, String value) throws SQLException {
        ps.setString(index, value);
    }

    /**
     * 获取long型值
     *
     * @param rs
     * @param index
     * @return
     * @throws java.sql.SQLException
     */
    public String getLongStringValue(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    /**
     * 获取clob型值
     *
     * @param rs
     * @param index
     * @return
     * @throws java.sql.SQLException
     */
    public String getClobStringValue(ResultSet rs, int index) throws SQLException {
        return rs.getString(index);
    }

    /**
     * 获得查询所有条数的sql
     *
     * @param sql
     * @return
     */
    public String getCountSql(String sql) {
        StringBuffer countSql = new StringBuffer("select count(*) from (");
        countSql.append(sql).append(") total");

        return countSql.toString();
    }

    public void setIdGenerator(IDGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    /**
     * 获得分页的sql
     * 由于各个数据库分页语句不同，故让子类自己实现此方法
     *
     * @param sql
     * @param start
     * @param num
     * @return
     */
    public abstract String getPageSql(String sql, int start, int num);

    /**
     * 通过sequence获取下一个主键的值<br/>
     * oracle:sequence《br/》
     * mysql：模拟的sequence表的表名
     *
     * @param sequenceName
     * @return
     */
    public abstract Object getNextId(String sequenceName);

    /**
     * 判断数据库中是否含有给定的表
     *
     * @param tableName  表名
     * @param dataSource 数据源
     * @return
     */
    public abstract String existTableSQL(String tableName, DataSource dataSource) throws Exception;

    public void setConvertBeans(List<ConvertBean> beans) {
        for (ConvertBean bean : beans) {
            try {
                Class<?> clazz = ClassUtils.forName(bean.getClazz(), Thread.currentThread().getContextClassLoader());
                Convert convert = (Convert) BeanUtils.instantiate(ClassUtils.forName(bean.getConvert(), Thread.currentThread().getContextClassLoader()));

                this.realConverts.put(clazz, convert);
            } catch (ClassNotFoundException e) {
                continue;
            }
        }
    }

    public <T extends ConvertGetter> Convert<T> getConvert(Class<?> clazz) {
        return this.realConverts.get(clazz);
    }
}
