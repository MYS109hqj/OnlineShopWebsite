

package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.scripts.BrowseHistoryGenerationScript;
import com.example.demo.scripts.OrderGenerationScript;

@SpringBootApplication
public class DemoApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Autowired
    private BrowseHistoryGenerationScript browseHistoryGenerationScript;

    // @Override
    // public void run(String... args) throws Exception {
    //     // 生成浏览记录
    //     browseHistoryGenerationScript.generateBrowseHistoryForProducts();
    // }

	@Autowired
	private OrderGenerationScript orderGenerationScript;

	@Override
	public void run(String... args) throws Exception {
		// 生成订单
		orderGenerationScript.generateOrders();
        // 生成浏览记录
        browseHistoryGenerationScript.generateBrowseHistoryForProducts();
	}
}
