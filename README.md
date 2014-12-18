##How to use

This library aims to provide a simple DSL over hibernate Criteria. 

##Examples
Saving single objects:
```
Employee employee=new Employee();
employee.setName("John");
employee.setAge(20);
employee.setDepartement(new Department("x"));
Hibutils.save(SessionFactory, employee);
```
Saving a list of objects:
```
List<Employee> employeeList=new LinkedList<>;
...
Hibutils.save(SessionFactory, employeeList);
```
Retrieve elements:
```
new HibQueryExecutor<Employee>()
                .from(Employee.class)
                .where(Restrictions.gt("age", 18))
                .listResult(SessionFactory);
```

Retrieve some fields:
```
new HibQueryExecutor<String>()
                .select("name")
                .from(Employee.class)
                .retClass(String.class)
                .where(Restrictions.gt("age", 18))
                .listResult(<SessionFactory>);
```

Retrieve some fields and map them on a class:
```
public class Person{
    private String name;
    private int age;
    ....
}

new HibQueryExecutor<Person>()
                .select("name","age")
                .from(Employee.class)
                .retClass(Person.class)
                .where(Restrictions.gt("age", 18))
                .listResult(<SessionFactory>);
```
