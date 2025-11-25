package taskmanager.core;

import taskmanager.exceptions.InvalidDataException;

public class Person
{
    private String id;
    private String name;
    private String phoneNumber;

    public Person(String id, String name)
    {
        if (id == null || id.trim().isEmpty())
        {
            throw new InvalidDataException("ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty())
        {
            throw new InvalidDataException("Name cannot be empty.");
        }

        this.id = id;
        this.name = name;
        this.phoneNumber = "Unprovided";
    }
    public Person(String id,  String name, String phoneNumber)
    {
        if (id == null || id.trim().isEmpty())
        {
            throw new InvalidDataException("ID cannot be empty.");
        }
        if (name == null || name.trim().isEmpty())
        {
            throw new InvalidDataException("Name cannot be empty.");
        }
        if (phoneNumber != null && !phoneNumber.matches("\\d+"))
        {
            throw new InvalidDataException("Phone number must contain only digits.");
        }

        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {return this.id;}
    public String getName() {return this.name;}
    public String getPhoneNumber() {return this.phoneNumber;}

    public String toString()
    {
        return "ID: " + this.id + " | Name: " + this.name;
    }
}
