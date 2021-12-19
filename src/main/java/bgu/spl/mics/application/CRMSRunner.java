package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import java.io.File;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        if (args.length == 0)
            throw new IllegalArgumentException("usage: <file_path>");
        List<StudentService> studentServiceList = new LinkedList<>();
        List<GPUService> gpuServiceList = new LinkedList<>();
        List<CPUService> cpuServiceList = new LinkedList<>();
        List<ConferenceService> conferenceServicesList = new LinkedList<>();
        TimeService timeService = null;
        int tickTime;
        int duration;
        List<Thread> threads = new LinkedList<>();

        //extracting data from json and building objects and services, subscribing to the massageBus when constructing the services
        File input = new File(args[0]); // "example_input.json"
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();

            //process all students
            JsonArray jsonStudentsArray = fileObject.get("Students").getAsJsonArray();
            for (JsonElement studentElement : jsonStudentsArray) {
                JsonObject studentObject = studentElement.getAsJsonObject();
                String name = studentObject.get("name").getAsString();
                String department = studentObject.get("department").getAsString();
                String status = studentObject.get("status").getAsString();

                //process each student models
                List<Model> models = new ArrayList<>();
                JsonArray jsonModelsArray = studentObject.get("models").getAsJsonArray();
                for (JsonElement modelElement : jsonModelsArray) {
                    JsonObject modelObject = modelElement.getAsJsonObject();
                    String modelName = modelObject.get("name").getAsString();
                    String modelType = modelObject.get("type").getAsString();
                    int modelSize = modelObject.get("size").getAsInt();
                    models.add(new Model(modelName, modelType, modelSize));
                }
                StudentService studentService = new StudentService(new Student(name, department, status, models));
                studentServiceList.add(studentService);
            }

            //process all gpus
            JsonArray jsonGpuArray = fileObject.get("GPUS").getAsJsonArray();
            for (JsonElement gpuElement : jsonGpuArray){
                String type = gpuElement.getAsString();
                GPUService gpuService = new GPUService(new GPU(type));
                gpuServiceList.add(gpuService);
            }

            //process all cpus
            JsonArray jsonCpuArray = fileObject.get("CPUS").getAsJsonArray();
            for (JsonElement cpuElement : jsonCpuArray){
                int type = cpuElement.getAsInt();
                CPUService cpuService = new CPUService(new CPU(type));
                cpuServiceList.add(cpuService);
            }

            //process all conferences
            JsonArray jsonConferenceArray = fileObject.get("Conferences").getAsJsonArray();
            for (JsonElement conferenceElement : jsonConferenceArray){
                JsonObject conferenceObject = conferenceElement.getAsJsonObject();
                String name = conferenceObject.get("name").getAsString();
                int date = conferenceObject.get("date").getAsInt();
                ConferenceService conferenceService = new ConferenceService(new ConfrenceInformation(name, date));
                conferenceServicesList.add(conferenceService);
            }

            //process tickTime and duration
            tickTime = fileObject.get("TickTime").getAsInt();
            duration = fileObject.get("Duration").getAsInt();
            timeService = TimeService.getFirstInstance(tickTime, duration);

        } catch (FileNotFoundException ex){
            System.err.println("File not found!");
        } catch (Exception ex){
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }

        //creating Tread for each service
        for (GPUService gpuService : gpuServiceList){
            Thread thread = new Thread(gpuService);
            threads.add(thread);
        }
        for (CPUService cpuService : cpuServiceList){
            Thread thread = new Thread(cpuService);
            threads.add(thread);
        }
        for (ConferenceService conferenceService : conferenceServicesList){
            Thread thread = new Thread(conferenceService);
            threads.add(thread);
        }
        for (StudentService studentService : studentServiceList){
            Thread thread = new Thread(studentService);
            threads.add(thread);
        }
        Thread clockThread = new Thread(timeService);
        threads.add(clockThread);

        //activates all treads and wait for them to finish
        for (Thread thread : threads){
            thread.start();
            try {
                thread.join(2);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try {
            for (Thread thread : threads){
                thread.join();
            }
        } catch (InterruptedException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        //creating the output string
        String output = "";
        for(StudentService studentService : studentServiceList) {
            output += studentService.toString() + "\n\n";
        }
        for (ConferenceService conferenceService : conferenceServicesList){
            output += conferenceService.toString() + "\n\n";
        }
        output += "GPU time used: " + Cluster.getInstance().getCpuTUUed() + "\n";
        output += "CPU time used: " + Cluster.getInstance().getGpuTUUed() + "\n";
        output += "Number of batches processed by all the CPUs: " +  Cluster.getInstance().getTotalDBProcessed() + "\n\n";

        //export the output to a text file
        String fileName = "ass2_output.txt";
        try {
            File outputFile = new File(fileName);
            if (outputFile.createNewFile()){
                System.out.println("File created: " + outputFile.getName());
            }
            else{
                System.out.println("File " + outputFile.getName() + " already exist.");
            }

            FileWriter myWriter = new FileWriter(fileName);
            myWriter.write(output);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }


}


