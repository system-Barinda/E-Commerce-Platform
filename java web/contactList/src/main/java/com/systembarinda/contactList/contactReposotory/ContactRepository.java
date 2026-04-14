package com.systembarinda.contactList.contactReposotory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.systembarinda.contactList.model.Contact;

public interface ContactRepository extends JpaRepository<Contact,Integer> {

}
