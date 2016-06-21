package iot.meetding.model;

import iot.meetding.ArduinoSerialPort;
import iot.meetding.controller.verifiers.RowVerify;
import iot.meetding.threads.Thread_CheckArduino;
import iot.meetding.threads.Thread_ReadData;
import iot.meetding.threads.Thread_RemoveData;
import iot.meetding.view.beans.ConfigItem;
import iot.meetding.view.beans.ConfigQuestion;
import iot.meetding.view.beans.WindowDataReadArduino;
import jssc.SerialPortException;
import jssc.SerialPortList;
import org.ini4j.Ini;
import org.ini4j.Profile;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Observable;

/**
 * Created by Rob on 18-5-2016.
 * IotModel for functions and data that is needed in multple places
 */
public class IoTmodel extends Observable implements Observer {


    //Dir name for files
    private final String DATA_DIR = "Klimaatscanner";
    // part of singleton pattern
    private static IoTmodel model;

    // list of serial ports
    private TreeMap<String,ArduinoSerialPort> ports;
    // active thread
    private  ArrayList<Thread> threads;
    // number of threads



    // config data
    private ArrayList<ConfigQuestion> questions;
    private ConfigItem<Integer> endTime;
    private ConfigItem<Integer> startTime;
    private ConfigItem<Integer> timeRangeMeasure;
    private ConfigItem<Integer> timeRangeQuestion;
    private ArrayList<ConfigItem<Boolean>> days;

    // selected comport
    private String comPort;

    // main windows frame
    private Frame frame;

    /**
     * private constructor
     */
    private IoTmodel() {
        threads = new ArrayList<>();
        comPort = "";
        ports = new TreeMap<>();

        questions = new ArrayList<>();

        // static config
        startTime = new ConfigItem<>("StartTime", 0);
        endTime = new ConfigItem<>("EndTime", 0);
        timeRangeMeasure = new ConfigItem<>("Measure", 0);
        timeRangeQuestion = new ConfigItem<>("QuestionTime", 0);
        days = new ArrayList<>();
        days.add(new ConfigItem<>("ma", false));
        days.add(new ConfigItem<>("di", false));
        days.add(new ConfigItem<>("wo", false));
        days.add(new ConfigItem<>("do", false));
        days.add(new ConfigItem<>("vr", false));
        days.add(new ConfigItem<>("za", false));
        days.add(new ConfigItem<>("zo", false));
    }

    public static IoTmodel getInstance(){
        if(model == null){
            model = new IoTmodel();
        }
        return model;
    }


    /**
     * Create a new question and add observer
     * @return ConfigQuestion object
     */
    public ConfigQuestion createQuestion(){
        ConfigQuestion q = new ConfigQuestion();
        q.addObserver(this);
        setChanged();
        notifyObservers();
        this.questions.add(q);
        return q;
    }

    /**
     * update Com ports
     * @param data window data object
     */
    public synchronized void updateComPorts(WindowDataReadArduino data) {
        if(threadRunning()){
            data.appendLogData("Er wordt al gezocht naar klimaatscanners");
            return;
        }

        HashSet<String> result = new HashSet<>();
        // get a simple list of all com ports on the system
        String[] portNames = SerialPortList.getPortNames();
        Thread t;
        removeAllPorts();
        // check every port for a arduino
        for (String portName : portNames) {
            // sometimes the serial library returns the same port multiple times.
            // we filter the result so only 1 port is used
            if (result.add(portName)) { // make sure only 1 thread per comport
                System.out.println(portName);
                t = new Thread_CheckArduino(new ArduinoSerialPort(portName),data);
                threads.add(t);
                t.start();

            }
        }
    }

    private boolean threadRunning(){
        for (Thread t : threads){
            if(t.isAlive()){
                return true;
            }
        }
        threads.clear();
        return false;
    }

    /**
     * Close com port.
     * @param portName the port name
     */
    private synchronized void removePort(String portName) {
        ArduinoSerialPort port = ports.remove(portName);
        try {
            port.closePort();
        } catch (SerialPortException | NullPointerException e) {
            System.out.println("Closing failed");
        }
    }

    /**
     * Remove all com ports
     */
    public void removeAllPorts(){
        for(String port : ports.keySet()){
            removePort(port);
        }
    }

    /**
     * Add a valid comport to the model
     * @param newPort the port
     */
    public synchronized void addPort(ArduinoSerialPort newPort) {
        // something bad happend ports must unique
        if (ports.containsKey(newPort.getPortName())) {
            removePort(newPort.getPortName());
        }
        ports.put(newPort.getPortName(), newPort);
        setChanged();
        notifyObservers(newPort.getPortName());
    }

    /**
     *
     * @param selectedItem, get a port
     * @return ArduinoSerialPort
     */
    private ArduinoSerialPort getPort(String selectedItem) {
        return ports.get(selectedItem);
    }




    /**
     * Start a thread for reading data from the arduino.
     * This only starts if no other thread is running.
     * @param data
     */
    public void startReadData(WindowDataReadArduino data) {
        if(threadRunning()){
            data.appendLogData("De applicatie is nog steeds bezig");
            return;
        }
        try {
            Thread t;
            t = new Thread_ReadData(model.getPort(comPort), data);
            threads.add(t);
            t.start();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start a thread for reading data from the arduino.
     * This only starts if no other thread is running.
     * @param data
     */
    public void startRemoveData(WindowDataReadArduino data) {
        if(threadRunning()){
            data.appendLogData("De applicatie is nog steeds bezig");
            return;
        }
        try {
            Thread t;
            t = new Thread_RemoveData(model.getPort(comPort), data);
            threads.add(t);
            t.start();
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    // getters for config
    public ArrayList<ConfigQuestion> getQuestions(){
        return questions;
    }
    public ConfigItem<Integer> getStartConfig(){
        return startTime;
    }
    public ConfigItem<Integer> getEndConfig(){
        return endTime;
    }
    public ArrayList<ConfigItem<Boolean>> getDayConfig(){
        return days;
    }
    public ConfigItem<Integer> getMeasurementsConfig(){
        return timeRangeMeasure;
    }
    public ConfigItem<Integer> getQuestionConfig(){
        return timeRangeQuestion;
    }


    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof ConfigQuestion){
            if(((ConfigQuestion) o).isDeleted()){
                questions.remove(o);
            }
        }
        setChanged();
        notifyObservers();
    }


    /**
     * Create a config file
     * @return byte[] with config
     */
    public ArrayList<byte[]> createConfigFile(){
        ArrayList<byte[]> result = new ArrayList<>();
        addQuestions(result);

        addConfig(result);

        return result;
    }


    /**
     * Add config to an arraylist of bytes
     * @param result
     */
    public void addConfig(ArrayList<byte[]> result){
        // time config
        result.add("[tijden]\n".getBytes());

        result.add(String.format("begintijd=%d\n", startTime.getValue()).getBytes());
        result.add(String.format("eindtijd=%d\n", endTime.getValue()).getBytes());
        result.add(String.format("vraaginterval=%d\n", timeRangeQuestion.getValue()).getBytes());
        result.add(String.format("sensorinterval=%d\n", timeRangeMeasure.getValue()).getBytes());
        result.add("\n".getBytes());

        // day config
        result.add("[dagen]\n".getBytes());
        for(ConfigItem<Boolean> day : days){
            result.add(String.format("%s=%b\n", day.getKey(), day.getValue()).getBytes());
        }
    }

    /**
     * Add questions to an arraylist of bytes
     * @param result Arraylist for result
     */
    public void addQuestions(ArrayList<byte[]> result){
        for(int i = 0; i<questions.size();i++){
            ConfigQuestion question = questions.get(i);
            result.add(String.format("[vraag%d]\n", i).getBytes());

            String[] parts = question.getQuestionParts();
            for(int x = 0; x<parts.length; x++){
                result.add(String.format("vraag_deel%d=%s\n", x, parts[x]).getBytes());
            }

            ArrayList<String[]> answers = question.getAnswers();
            for(int y =0; y<answers.size();y++){
                String[] answerParts = answers.get(y);

                for(int z = 0; z<answerParts.length; z++){
                    result.add(String.format("antwoord%d_deel%d=%s\n", y,z, answerParts[z]).getBytes());
                }

            }
            result.add("\n".getBytes());



        }
    }

    /**
     * Get directory for data files
     * @return File
     */
    public File getDataDir() throws IOException {
        // create file objects
        String dir = String.format("%1$s%2$s%3$s", System.getProperty("user.home"), File.separator, DATA_DIR);
        File f = new File(dir);
        // create storage directory
        if(!f.exists()){
            if(!f.mkdirs()){
                throw new IOException("IO exception, cannot create directory");
            }
        }
        return f;
    }


    /**
     * Get directory for config files
     * @return File
     */
    public File getConfigDir() throws IOException {
        File f = getDataDir();
        File configDir = new File(f, "config");
        // create storage directory
        if(!configDir.exists()){
            if(!configDir.mkdirs()){
                throw new IOException("IO exception, cannot create directory");
            }
        }
        return configDir;
    }


    /**
     * Read config a config file
     * @param config The config file to read
     */
    public void readConfig(File config){

        if(!config.exists()){
            setChanged();
            notifyObservers();
            return;
        }
        // clear data
        questions.clear();
        try {
            // parse ini file
            Ini ini = new Ini();
            ini.load(new FileReader(config));
            // check every section
            for(String section : ini.keySet()){
                if(section.startsWith("vraag")){
                    readQuestion(ini.get(section));
                } else if (section.equals("tijden")) {
                    readTimeConfig(ini.get(section));
                } else if (section.equals("dagen")) {
                    readDayConfig(ini.get(section));
                }
            }
            setChanged();
            notifyObservers();

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Parse the day section of the ini file.
     * @param section, The section
     */
    private void readDayConfig(Profile.Section section) {
        if(!section.getName().equals("dagen")){
            return; // wrong section given
        }
        for(String key : section.keySet()){
            for(ConfigItem<Boolean> day : days){
                if(day.getKey().equals(key)){
                    day.setValue(Boolean.parseBoolean(section.get(key)));
                    break;
                }
            }
        }
    }

    /**
     * Read time config
     * @param s the section
     */
    private void readTimeConfig(Profile.Section s) {
        if(!s.getName().equals("tijden")){
            return;
        }
        startTime.setValue(Integer.parseInt(s.get("begintijd")));
        endTime.setValue(Integer.parseInt(s.get("eindtijd")));
        timeRangeMeasure.setValue(Integer.parseInt(s.get("sensorinterval")));
        timeRangeQuestion.setValue(Integer.parseInt(s.get("vraaginterval")));
    }

    /**
     * Read questions from section
     * @param section the section
     */
    private void readQuestion(Profile.Section section){
        // create a new question
        ConfigQuestion question = createQuestion();
        // create answer list
        ArrayList<String[]> answers = new ArrayList<>();


        for(String key : section.keySet()){
            // if key start with vraag_deel, its a part of a question;
            if(key.startsWith("vraag_deel")){
                // get the question part number
                String num = key.substring(key.length()-1);
                // set questoin
                question.setQuestion(Integer.parseInt(num), section.get(key));

            } else if (key.startsWith("antwoord")) {
                // if key start with antwoord

                int answerNumb, answerPart;
                // get the answer number ans part
                answerNumb = Integer.parseInt(key.substring(8, key.indexOf("_")));
                answerPart = Integer.parseInt(key.substring(key.length() - 1));
                // create an answer array
                while(answers.size() < answerNumb+1){
                    answers.add(new String[ConfigQuestion.ROWS_ANSWER]);
                }
                // set answer
                answers.get(answerNumb)[answerPart] = section.get(key);
            }
        }
        question.setAnswers(answers);
    }

    /**
     * set selected comport
     * @param comPort, the comport name
     */
    public void setComPort(String comPort) {
        this.comPort = comPort;
    }


    /**
     * Validate the config
     * @return, true of false
     */
    public boolean checkConfig(){
        if(questions.size() == 0){
            return false;
        }
        for(ConfigQuestion q : questions){
            if(q.getAnswers().size() == 0){
                return false;
            }
            for(String[] answer : q.getAnswers()){
                for(String s : answer){
                    if(s.length() > RowVerify.MAX_LENGTH){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    // get, set frame
    public Frame getFrame() {
        return frame;
    }
    public void setFrame(Frame frame){
        this.frame = frame;
    }

    // get a comport
    public ArduinoSerialPort getComPort() {
        return getPort(comPort);
    }
}


