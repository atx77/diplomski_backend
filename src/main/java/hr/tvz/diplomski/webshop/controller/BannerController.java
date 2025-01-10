package hr.tvz.diplomski.webshop.controller;

import hr.tvz.diplomski.webshop.service.BannerService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/banner")
@CrossOrigin(origins = "http://localhost:4200")
public class BannerController {

    @Resource
    private BannerService bannerService;

    @RequestMapping(method = RequestMethod.GET)
    public List<String> getAllBanners() {
        return bannerService.getAllBanners();
    }
}
