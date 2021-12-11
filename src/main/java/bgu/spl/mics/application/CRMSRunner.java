package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.LinkedList;
import java.util.List;

import java.io.File;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {
    public static void main(String[] args) {
        List<StudentService> studentServiceList = new LinkedList<>();
        List<GPUService> gpuServiceList = new LinkedList<>();
        List<CPUService> cpuServiceList = new LinkedList<>();
        List<ConferenceService> conferenceServicesList = new LinkedList<>();
        TimeService timeService;
        int tickTime;
        int duration;

        //extracting data from json and building objects and services, subscribing to the massageBus when constructing the services
        File input = new File("example_input.json"); // args[0]
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
                List<Model> models = new LinkedList<>();
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
                //JsonObject gpuObject = gpuElement.getAsString();
                String type = gpuElement.getAsString();
                GPUService gpuService = new GPUService(new GPU(type));
                gpuServiceList.add(gpuService);
            }

            //process all cpus
            JsonArray jsonCpuArray = fileObject.get("CPUS").getAsJsonArray();
            for (JsonElement cpuElement : jsonCpuArray){
                //JsonObject cpuObject = cpuElement.getAsJsonObject();
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

        //activates all treads and the clock thread


        //creating the output string
        String output = "";
        for(StudentService studentService : studentServiceList) {
            output += studentService.toString() + "\n\n";
        }
        //output += "\n";
        for (ConferenceService conferenceService : conferenceServicesList){
            output += conferenceService.toString() + "\n";
        }
        output += "\n";
        int gpuUseTime = 0;
        for (GPUService gpuService : gpuServiceList){
            gpuUseTime += gpuService.getGpuUseTime();
        }
        output += "GPU time used: " + gpuUseTime + "\n";
        int cpuUseTime = 0;
        int batchesProcessed = 0;
        for (CPUService cpuService : cpuServiceList){
            cpuUseTime += cpuService.getCpuUseTime();
            batchesProcessed += cpuService.getBatchesProcessed();
        }
        output += "CPU time used: " + cpuUseTime + "\n";
        output += "\n" + "Number of batches processed by all the CPUs: " + batchesProcessed + "\n\n";


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


