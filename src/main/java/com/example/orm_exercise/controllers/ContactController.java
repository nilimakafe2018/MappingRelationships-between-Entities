package com.example.orm_exercise.controllers;

import com.example.orm_exercise.models.Address;
import com.example.orm_exercise.models.Contact;
import com.example.orm_exercise.repositories.ContactRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {
    private final ContactRepository contactRepository;

    public ContactController(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @GetMapping
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contact getContactById(@PathVariable int id) {
        return contactRepository.findById(id).orElse(null);
    }

    @PostMapping
    public Contact createContact(@RequestBody Contact contact) {
        if (contact.getAddresses() != null) {
            contact.getAddresses().forEach(addr -> addr.setContact(contact));
        }
        return contactRepository.save(contact);
    }

    @PutMapping("/{id}")
    public Contact updateContact(@PathVariable int id, @RequestBody Contact updatedContact) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(updatedContact.getName());
            contact.setEmail(updatedContact.getEmail());
            contact.setPhoneNumber(updatedContact.getPhoneNumber());
            if (updatedContact.getAddresses() != null) {
                contact.getAddresses().clear();
                updatedContact.getAddresses().forEach(addr -> {
                    addr.setContact(contact);
                    contact.getAddresses().add(addr);
                });
            }
            return contactRepository.save(contact);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deleteContact(@PathVariable int id) {
        contactRepository.deleteById(id);
    }

    // BONUS: Add a new address to a contact
    @PostMapping("/{contactId}/addresses")
    public Contact addAddress(@PathVariable int contactId, @RequestBody Address address) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
        address.setContact(contact);
        contact.getAddresses().add(address);
        return contactRepository.save(contact);
    }

    // BONUS: Delete an address from a contact
    @DeleteMapping("/{contactId}/addresses/{addressId}")
    public Contact deleteAddress(@PathVariable int contactId, @PathVariable int addressId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found"));

        contact.getAddresses().removeIf(addr -> addr.getId() == addressId);
        return contactRepository.save(contact);
    }
}
