package kz.project.mymarket.controllers;

import kz.project.mymarket.dto.ProductDTO;
import kz.project.mymarket.entities.Category;
import kz.project.mymarket.entities.User;
import kz.project.mymarket.repositories.UserRepository;
import kz.project.mymarket.services.CategoryService;
import kz.project.mymarket.services.OrderService;
import kz.project.mymarket.services.ProductService;
import kz.project.mymarket.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class UiController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final OrderService orderService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullname,
                           @RequestParam String email,
                           @RequestParam String password,
                           Model model) {
        if (userRepository.existsByEmail(email)) {
            model.addAttribute("error", "This email is already registered");
            return "register";
        }
        User user = new User();
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");
        userRepository.save(user);
        model.addAttribute("success", "Registration successful. You can now log in.");
        return "register";
    }

    @GetMapping("/products")
    public String products(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        return "products";
    }

    @PostMapping("/products/add")
    public String addProduct(@RequestParam String name,
                             @RequestParam Double price,
                             @RequestParam Integer quantity,
                             @RequestParam Long categoryId,
                             RedirectAttributes redirectAttributes) {
        try {
            ProductDTO dto = new ProductDTO();
            dto.setName(name);
            dto.setPrice(price);
            dto.setQuantity(quantity);
            dto.setCategoryId(categoryId);
            productService.save(dto);
            redirectAttributes.addFlashAttribute("success", "Product added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("product", productService.findById(id));
            model.addAttribute("categories", categoryService.findAll());
            return "product-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/products";
        }
    }

    @PostMapping("/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam Double price,
                                @RequestParam Integer quantity,
                                @RequestParam Long categoryId,
                                RedirectAttributes redirectAttributes) {
        try {
            ProductDTO dto = new ProductDTO();
            dto.setName(name);
            dto.setPrice(price);
            dto.setQuantity(quantity);
            dto.setCategoryId(categoryId);
            productService.update(id, dto);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products";
    }

    @PostMapping("/products/buy/{id}")
    public String buyProduct(@PathVariable Long id,
                             @RequestParam(defaultValue = "1") Integer quantity,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            orderService.createSingleProductOrder(authentication.getName(), id, quantity);
            redirectAttributes.addFlashAttribute("success", "Order created successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/products";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category();
            category.setName(name);
            categoryService.save(category);
            redirectAttributes.addFlashAttribute("success", "Category added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoryPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("category", categoryService.findById(id));
            return "category-edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/ui/categories";
        }
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @RequestParam String name,
                                 RedirectAttributes redirectAttributes) {
        try {
            Category category = new Category();
            category.setName(name);
            categoryService.update(id, category);
            redirectAttributes.addFlashAttribute("success", "Category updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/categories";
    }

    @GetMapping("/orders")
    public String orders(Model model, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("orders", admin ? orderService.findAll() : orderService.findByUserEmail(authentication.getName()));
        model.addAttribute("isAdmin", admin);
        return "orders";
    }

    @PostMapping("/orders/status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam String status,
                                    RedirectAttributes redirectAttributes) {
        try {
            orderService.updateStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Order status updated successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/orders";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            orderService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Order deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/orders";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @PostMapping("/users/add")
    public String addUser(@RequestParam String fullname,
                          @RequestParam String email,
                          @RequestParam String password,
                          @RequestParam(defaultValue = "USER") String role,
                          RedirectAttributes redirectAttributes) {
        try {
            User user = new User();
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPassword(password);
            user.setRole(role);
            userService.save(user);
            redirectAttributes.addFlashAttribute("success", "User added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                             Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findById(id);
            if (user.getEmail().equals(authentication.getName())) {
                throw new IllegalArgumentException("You cannot delete your own account");
            }
            userService.delete(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/ui/users";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", admin);
        model.addAttribute("productCount", productService.findAll().size());
        model.addAttribute("categoryCount", categoryService.findAll().size());
        model.addAttribute("ordersTitle", admin ? "All orders" : "My orders");
        model.addAttribute("orderCount", admin ? orderService.findAll().size() : orderService.findByUserEmail(authentication.getName()).size());
        model.addAttribute("userCount", admin ? userService.findAll().size() : 0);
        model.addAttribute("recentOrders", (admin ? orderService.findAll() : orderService.findByUserEmail(authentication.getName()))
                .stream()
                .limit(5)
                .toList());
        return "dashboard";
    }
}
