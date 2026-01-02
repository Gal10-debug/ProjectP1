package test;


import java.io.*;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GenericConfig implements Config {
    private String m_confFile;
    private List<ParallelAgent> agents = new ArrayList<>();

    public void setConfFile(String confFile) {
        m_confFile = confFile;
    }

    @Override
    public void create(){
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(m_confFile));
            if(lines.size()%3!=0){
                throw new RuntimeException("Invalid config file");
            }

            for (int i=0;i<lines.size();i+=3){
                String className = lines.get(i).trim();
                String[] subs = lines.get(i+1).trim().split(",");
                String[] pubs =  lines.get(i+2).trim().split(",");

                Class<?> clazz = Class.forName(className);
                Constructor<?> ctor =
                        clazz.getConstructor(String[].class, String[].class);
                Agent agent = (Agent) ctor.newInstance(subs,pubs);

                ParallelAgent pa = new ParallelAgent(agent);
                pa.start();
                agents.add(pa);
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void close() {
        for(ParallelAgent pa:agents){
            pa.close();
        }
        agents.clear();
    }
}
