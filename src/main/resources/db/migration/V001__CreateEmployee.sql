create table department
(
    id   int primary key auto_increment,

    name varchar(255) not null
);

create table employee
(
    id            int primary key auto_increment,

    last_name     varchar(255) not null,
    first_name    varchar(255) not null,

    department_id int          not null,

    foreign key (department_id) references department (id)
);

create view v_employee as
select e.id as employee_id, e.first_name as employee_first_name, e.last_name as employee_last_name, d.name as department_name
from employee e
join department d on e.department_id = d.id;
