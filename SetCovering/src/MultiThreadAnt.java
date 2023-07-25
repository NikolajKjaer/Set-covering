public class MultiThreadAnt extends Thread {
    Instance instance;
    double[] phero;
    int alpha;
    int beta;
    double q0;
    Ant ant;

    public MultiThreadAnt(Instance instance, double[] phero, int alpha, int beta, double q0){
        this.instance = instance;
        this.phero = phero;
        this.alpha = alpha;
        this.beta = beta;
        this.q0 = q0;
    }

    @Override
    public void run(){
        ant = new Ant(instance, phero, alpha, beta, q0);
        this.ant.constructSolution();
        this.ant.localSearch(2);  // choose 1, 2 or 3 steps
        this.ant.getCost();
    }
}
