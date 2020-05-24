package net.zey.bettersql.request;

import net.zey.bettersql.arguments.TableArguments;
import net.zey.bettersql.condition.EqualCondition;
import net.zey.bettersql.condition.RepCondition;
import net.zey.bettersql.database.SQLObject;
import net.zey.bettersql.database.Table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SelectRequest extends Request{

    public SelectRequest(Table table, StringBuilder sql) {
        super(table, sql);
    }

    public SelectRequest where(String name, SQLObject sql){
        setCondition(new EqualCondition(sql, name));
        return this;
    }

    public List<SQLObject> sendSql() {
        try {
            if (getCondition() != null) {
                getSql().append(getCondition().getAdding());
            }
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Connection conn = DriverManager.getConnection(getTable().getData().getURL());
            PreparedStatement p = conn.prepareStatement(getSql().toString());

            if (getCondition() != null) {
                if (getCondition() instanceof RepCondition) {
                    RepCondition cond = (RepCondition) getCondition();
                    set(1, cond.getObj(), p);
                }
            }

            ResultSet r = p.executeQuery();

            List<SQLObject> all = new ArrayList<>();
            Table t = getTable();
            while (r.next()) {
                for (TableArguments tb : t.getArgs()) {
                    SQLObject o = get(tb, r);
                    all.add(o);
                }
            }

            return all;
        }catch(Exception e){
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
