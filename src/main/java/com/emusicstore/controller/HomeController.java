package com.emusicstore.controller;

import com.emusicstore.dao.ProductDao;
import com.emusicstore.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


@Controller
public class HomeController {

    @Autowired
    private ProductDao productDao;

    private Path path;

    @RequestMapping("/")
    public String home() {
        return "home";
    }


    @RequestMapping("/productList")
    public String getProducts(Model model) {
        List<Product> products = productDao.getAllProducts();
        model.addAttribute("products", products);

        return "productList";
    }

    @RequestMapping("/productList/viewProduct/{productId}")
    public String viewProduct(@PathVariable int productId, Model model) throws IOException {

        Product product = productDao.getProductById(productId);
        model.addAttribute(product);

        return "viewProduct";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }

    @RequestMapping("/admin/productInventory")
    public String productInventory(Model model) {
        List<Product> products = productDao.getAllProducts();
        model.addAttribute("products", products);

        return "productInventory";
    }


    //showing the form
    @GetMapping("/admin/productInventory/addProduct")
    public String addProduct(Model model) {
        Product product = new Product();
        //these are radio buttons so it's good to set some default values
        product.setProductCategory("instrument");
        product.setProductCondition("new");
        product.setProductStatus("active");

        model.addAttribute("product", product);

        return "addProduct";
    }

    //Posting and processing the form
    @PostMapping("/admin/productInventory/addProduct")
    public String addProductPost(@ModelAttribute("product") Product product, HttpServletRequest request) {
        productDao.addProduct(product);
        //save product image
        MultipartFile productImage = product.getProductImage();

        //getting the root file directory in the server because this is dynamic it will be different when uploaded in other servers
        String rootDirectory = request.getSession().getServletContext().getRealPath("/");

        //getting the path to the image
        path = Paths.get(rootDirectory + "WEB-INF/resources/images/picture" + product.getProductId() + ".png");

        if (productImage != null && !productImage.isEmpty()) {
            try {
                productImage.transferTo(new File(path.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Product image saving failed", e);
            }
        }

        return "redirect:/admin/productInventory";
    }

    //Posting and processing the form
    @GetMapping("/admin/productInventory/deleteProduct/{productId}")
    public String addProductPost(@PathVariable("productId") int id,  HttpServletRequest request) {

        //getting the root file directory in the server because this is dynamic it will be different when uploaded in other servers
        String rootDirectory = request.getSession().getServletContext().getRealPath("/");

        //getting the path to the image
        path = Paths.get(rootDirectory + "WEB-INF/resources/images/picture" + id + ".png");

        if (Files.exists(path)){
            try {
                Files.delete(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productDao.deleteProduct(id);
        return "redirect:/admin/productInventory";
    }

}
