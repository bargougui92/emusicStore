package com.emusicstore.controller;

import com.emusicstore.dao.ProductDao;
import com.emusicstore.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private ProductDao productDao;

    private Path path;

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
    public String addProductPost(@Valid @ModelAttribute("product") Product product,
                                 BindingResult result,
                                 HttpServletRequest request) {
        if(result.hasErrors()){
            return "addProduct";
        }
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
    public String addProductPost(@PathVariable("productId") int id, HttpServletRequest request) {

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

    @GetMapping("/admin/productInventory/editProduct/{productId}")
    public String editProduct(@PathVariable("productId") int id, Model model) {
        Product product = productDao.getProductById(id);

        model.addAttribute(product);
        return "editProduct";
    }

    @PostMapping("/admin/productInventory/editProduct")
    public String editProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result,
                              HttpServletRequest request,
                              Model model) {

        if(result.hasErrors()){
            return "editProduct";
        }
        MultipartFile productImage = product.getProductImage();
        String rootDirectory = request.getSession().getServletContext().getRealPath("/");
        path = Paths.get(rootDirectory + "WEB-INF/resources/images/picture" + product.getProductId() + ".png");
        model.addAttribute(product);
        if (productImage != null && !productImage.isEmpty()) {
            try {
                productImage.transferTo(new File(path.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Product image saving failed", e);
            }
        }
        productDao.editProduct(product);
        return  "redirect:/admin/productInventory";
    }

}
