package com.example.controller;

import io.lettuce.core.api.sync.RedisCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.lettuce.RedisCli;
import com.example.mapper.AuthorMapper;
import com.example.model.Author;

@RestController
public class AuthorController {
	
	Logger logger = LoggerFactory.getLogger(AuthorController.class);
	
	@Autowired
	AuthorMapper authorMapper;
	
	@GetMapping("/authors/{id}")
	public Author getOneAuthor(@PathVariable int id) {
		logger.debug("--------hello-----------");
		return authorMapper.findOneAuthor(id);
	}
	
	@GetMapping("/authors")
	public List<Author> getAllAuthors(){
		List<Author> authors = null;
		authors = readFromRedis();
		if(authors.size()==0){
			logger.debug("----------read from database-------------------");
			authors =  authorMapper.findAllAuthors();
			writeToRedis(authors);
		}
		return authors;
	}
	
	
	
	private List<Author> readFromRedis() {
		RedisCommands<String, String> syncCommands = RedisCli.connection.sync();
		List<Author> authors = new ArrayList();
		List<String> keys = syncCommands.keys("author*");
		if(keys.size()==0) return authors;
		
		for(String key: keys){
			Map<String, String> map = syncCommands.hgetall(key);
			Author author = new Author();
			author.setId( Integer.valueOf(map.get("id")) );
			author.setName( map.get("name") );
			author.setPhone( map.get("phone") );
			authors.add(author);
		}
		logger.debug("----------read from redis-------------------");
		return authors;
	}

	private void writeToRedis(List<Author> authors) {
		RedisCommands<String, String> syncCommands = RedisCli.connection.sync();
		
		for(Author auth: authors){
			Map<String, String> map = new HashMap();
			map.put("id", String.valueOf(auth.getId()));
			map.put("name", auth.getName());
			map.put("phone", auth.getPhone());
			syncCommands.hmset("author:"+auth.getId(), map);
		}
		
		
	}
	
	
	@PostMapping(value="/authors", consumes="application/json")
	public int addAuthor(@RequestBody Author author){
		return authorMapper.insertAuthor(author);
	}
	
	
	@DeleteMapping("/authors/{id}")
	public int delAuthor(@PathVariable int id){
		return authorMapper.deleteAuthor(id);
	}
	
	
	
}
