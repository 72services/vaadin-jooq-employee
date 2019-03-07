create table department
(
  id   int primary key auto_increment,
  name varchar(255) not null
);

create table employee
(
  id            int primary key auto_increment,
  name          varchar(255) not null,
  department_id int          not null,

  foreign key (department_id) references department (id)
);