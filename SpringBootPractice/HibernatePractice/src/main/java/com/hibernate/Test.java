package com.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


public class Test {

	public static void main(String[] args) {
		System.out.println("Hibernate Stared.....");
		
		SessionFactory sf = new Configuration().configure().buildSessionFactory();
		Session session = sf.openSession();
		Transaction tx = session.beginTransaction();
		
		Books book = new Books();
		
		//book.setBookId(123);
		book.setBookName("java");
		book.setAuthor("Neeraj");
		book.setPrice("250");
		
		session.save(book);
		tx.commit();
		session.close();
		
		System.out.println("Hibernate end.........");
		

	}

}
