package com.demo.urlshortener.api;

import java.util.Base64;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlShortnerController {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortnerController.class);
	
	@Autowired
	RedisTemplate<String,String> redisTemplate;
	
	
	@GetMapping("/id/{id}")
	public String getUrl(@PathVariable("id") String id) {
		String url = (String) redisTemplate.opsForValue().get(id);
		LOGGER.info(" {} Found from redis", id);
		if(url != null)
		return url;
		
		return ("No encoding found");
	}

	@PostMapping("/create")
	public String create(@RequestBody String url) {

		UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
		
		if(urlValidator.isValid(url)) {
			String encodedUrl = Base64.getUrlEncoder().encodeToString(url.getBytes());
			
			LOGGER.info("Url: {} ,\n Encoded Url: {}", url,encodedUrl);
			
			redisTemplate.opsForValue().set(encodedUrl, url);
			return encodedUrl;
		}

		throw new RuntimeException("Invalid Url : " + url);
	}

}
