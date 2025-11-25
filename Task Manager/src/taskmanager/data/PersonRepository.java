package taskmanager.data;

import taskmanager.core.Person;
import taskmanager.exceptions.RepositoryException;

import java.util.ArrayList;

public class PersonRepository {
    private ArrayList<Person> people;

    public PersonRepository() {
        people = new ArrayList<>();
        // Preload 4 team members
        people.add(new Person("1", "Filip", "1234567890"));
        people.add(new Person("2", "Kristian", "0987654321"));
        people.add(new Person("3", "Angelina", "1122334455"));
        people.add(new Person("4", "Kaloyan", "5566778899"));
    }

    public PersonRepository(ArrayList<Person> people) {
        if(people.size() != 4) {
            throw new IllegalArgumentException("People must have 4 elements");
        }

        this.people = people;
    }

    public boolean exists(String id) {
        for(int i = 0; i < people.size(); i++) {
            if(people.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public void addPerson(Person person) {
        if(!exists(person.getId())) people.add(person);
        else throw new RepositoryException("Person already exists");
    }

    public ArrayList<Person> getPeople() {
        return people;
    }

    public Person findById(String id) {
        for(int i = 0; i < people.size(); i++) {
            if(people.get(i).getId().equals(id)) {
                return people.get(i);
            }
        }
        return null;
    }
}
