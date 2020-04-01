package junit;

import dao.CategoryDao;
import dao.impl.CategoryDAOImpl;
import domain.Category;
import org.junit.Test;

import java.util.List;

/**
 * Created by ozc on 2017/4/12.
 */
public class CategoryDemo {

    @Test
    public void add() {

        Category category = new Category();
        category.setId("2");
        category.setName("数据库系列");
        category.setDescription("这是数据库系列");

        CategoryDao category1 = new CategoryDAOImpl();
        category1.addCategory(category);

    }

    @Test
    public void find() {

        String id = "1";
        CategoryDao category1 = new CategoryDAOImpl();
        Category category = category1.findCategory(id);

        System.out.println(category.getName());
    }
    @Test
    public void getAll() {


        CategoryDao category1 = new CategoryDAOImpl();
        List<Category> categories = category1.getAllCategory();

        for (Category category : categories) {
            System.out.println(category.getName());
        }
    }



}
