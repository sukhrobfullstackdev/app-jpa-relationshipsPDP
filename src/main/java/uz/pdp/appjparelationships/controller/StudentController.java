package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;

import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    final StudentRepository studentRepository;
    final GroupRepository groupRepository;
    final AddressRepository addressRepository;

    public StudentController(StudentRepository studentRepository, GroupRepository groupRepository, AddressRepository addressRepository) {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.addressRepository = addressRepository;
    }

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT

    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentsForFaculty(@PathVariable Integer facultyId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping("/forGroup/{groupId}")
    public Page<Student> getStudentsForGroup(@PathVariable Integer groupId, @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupId, pageable);
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto studentDto) {
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            Address address = new Address(studentDto.getCity(), studentDto.getDistrict(), studentDto.getStreet());
            addressRepository.save(address);
            Student student = new Student(studentDto.getFirstName(), studentDto.getLastName(), address, group);
            studentRepository.save(student);
            return "The student has been successfully edited!";
        } else {
            return "The group has not been found to add a new student to it!";
        }
    }

    @PutMapping(value = "/{id}")
    public String editStudent(@RequestBody StudentDto studentDto, @PathVariable Integer id) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (optionalStudent.isPresent() && optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            Student student = optionalStudent.get();
            Address address = student.getAddress();
            address.setCity(studentDto.getCity());
            address.setDistrict(studentDto.getDistrict());
            address.setStreet(studentDto.getStreet());
            addressRepository.save(address);
            student.setGroup(group);
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            studentRepository.save(student);
            return "The student has been successfully edited!";
        } else {
            return "The student or group which you want to assign to this student has not been found to edit!";
        }
    }

    @DeleteMapping(value = "/{id}")
    public String deleteStudent(@PathVariable Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            Address address = student.getAddress();
            addressRepository.delete(address);
            studentRepository.delete(student);
            return "The student has been successfully deleted!";
        } else {
            return "The student has not been found to delete!";
        }
    }
}
