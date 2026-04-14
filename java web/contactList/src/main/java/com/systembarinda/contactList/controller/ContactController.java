package com.systembarinda.contactList.controller;


import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.systembarinda.contactList.contactReposotory.ContactRepository;
import com.systembarinda.contactList.model.Contact;



@Controller
public class ContactController {
	private final ContactRepository repo;
	public ContactController(ContactRepository repo) {
		this.repo = repo;
	}
	@GetMapping("/")
	public String index() {
		return "helloWorld";
	}		
	@GetMapping("/add") 
	public String addContact(Model model) {
		Contact contact = new Contact();
		model.addAttribute("contact", contact);
		return "add";
	}
	@PostMapping("/save") 
	public String saveContact(Contact contact) {
		repo.save(contact);
		return "redirect:/add";
	}
	@GetMapping("/list") 
	public String showContact(Model model) {
		List <Contact> contact = repo.findAll(Sort.by("name").descending());
		
		model.addAttribute("list", contact);
		
		return "list";
	}
	@GetMapping("/delete/{id}") 
	public String deleteContact(@PathVariable int id) {
		
		repo.deleteById(id);
		return "redirect:/list";
	}
	@GetMapping("/edit/{id}") 
	public String editContact(@PathVariable int id,Model model) {
		
		model.addAttribute("contact",repo.findById(id));
		return "/edit";
	}
	@PostMapping("/update") 
	public String updateContact(Contact contact) {
		repo.save(contact);
		return "redirect:/list";
	}
     	
  
}
