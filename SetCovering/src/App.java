import java.util.ArrayList;

public class App {

    public static void main(String[] args) throws Exception {
        Instance myInstance = new Instance("Data/scp41.txt");
        Ant solution = solve(myInstance, 25, 100, 1, 5, 0.5, 0.2);
        System.out.println("The best objective value found is: "+solution.objVal);
    }

    public static Ant solve(Instance myInstance, int numAnts, int pheroStr, int alpha, int beta, double rho, double q0){

        // Initialising pheromone trail
        double[] phero = new double[myInstance.numCols];
        for(int i=0;i<myInstance.numCols;i++){
            phero[i] = 1;
        }
        Ant bestAnt = new Ant(myInstance, phero, alpha, beta, q0);
        bestAnt.constructSolution();
        bestAnt.getCost();
        Integer objBest = bestAnt.objVal;  // For tracking best obj val
        int numIterations = 100;

        int no_improve = 0;

        for(int it=0;it<numIterations;it++){
            no_improve++;
            ArrayList<Ant> antList = new ArrayList<Ant>();
            ArrayList<Integer> antObj = new ArrayList<Integer>();

            // Construct solutions (ants)
            ArrayList<MultiThreadAnt> threadAnts = new ArrayList<MultiThreadAnt>();
            for(int antID=0;antID<numAnts;antID++){
                MultiThreadAnt threadAnt = new MultiThreadAnt(myInstance, phero, alpha, beta, q0);
                threadAnt.start();
                threadAnts.add(threadAnt);
            }
            for(MultiThreadAnt threadAnt : threadAnts){
                try {
                    threadAnt.join();

                    Ant ant = threadAnt.ant;

                    antList.add(ant);
                    antObj.add(ant.objVal);
                } catch (InterruptedException e) {
                }
            }

            // Update best ant found so far
            int bestAntIdx = argmin(antObj);
            if(antObj.get(bestAntIdx) < objBest){
                objBest = antObj.get(bestAntIdx);
                bestAnt = antList.get(bestAntIdx);
                no_improve = 0;
            }

            System.out.println("Iteration number: "+it
                               +" ; Best obj current iter: " + antObj.get(bestAntIdx)
                               +" ; Best obj all iter: " + objBest);

            // Update pheromone trail
            double[] deltaTauSummed = new double[myInstance.numCols];
            for(Ant ant : antList){
                for(int col : ant.usedCols){
                    deltaTauSummed[col] += pheroStr / myInstance.cost.get(col);
                }
            }
            for(int i=0;i<myInstance.numCols;i++){
                phero[i] = rho * phero[i] + deltaTauSummed[i];
            }
            
            // If improvement has not been seen recently, diversify search by reducing pheromone
            int worstAntIdx = argmax(antObj);
            Ant worstAnt = antList.get(worstAntIdx);
            if(no_improve >= 3){
                for(int col : worstAnt.usedCols){
                    phero[col] = 1;
                }
            }
        }

        return bestAnt;

    }

    public static int argmin(ArrayList<Integer> array){
        int idxMinVal = 0;
        int minVal = array.get(idxMinVal);

        for(int i=1;i<array.size();i++){
            if(array.get(i) < minVal){
                idxMinVal = i;
                minVal = array.get(idxMinVal);
            }
        }
        
        return idxMinVal;
    }

    public static int argmax(ArrayList<Integer> array){
        int idxMaxVal = 0;
        int maxVal = array.get(idxMaxVal);

        for(int i=1;i<array.size();i++){
            if(array.get(i) > maxVal){
                idxMaxVal = i;
                maxVal = array.get(idxMaxVal);
            }
        }
        
        return idxMaxVal;
    }

}
