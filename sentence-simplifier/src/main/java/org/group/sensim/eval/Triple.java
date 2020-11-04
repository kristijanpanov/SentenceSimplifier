package org.group.sensim.eval;

/**
 * Class which represents a relation (triple): <subject, predicate, object>
 */
public class Triple {
    private String subject;
    private String predicate;
    private String object;

    public Triple(String s, String p, String o){
        this.subject = s;
        this.predicate = p;
        this.object = o;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Triple {" +
                "subject=" + subject +
                ", predicate=" + predicate +
                ", object=" + object +
                '}';
    }
}
