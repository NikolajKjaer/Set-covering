import java.util.ArrayList;
import java.util.Random;

public class Ant {
    
    ArrayList<Integer> usedCols = new ArrayList<Integer>();
    ArrayList<Integer> uncovRows = new ArrayList<Integer>();
    ArrayList<Integer> unusedCols = new ArrayList<Integer>();
    ArrayList<Integer> numRowsCovByCol = new ArrayList<Integer>();
    int objVal;
    Instance instance;
    double[] phero;
    int alpha;
    int beta;
    double q0;


    Ant(Instance instance, double[] phero, int alpha, int beta, double q0){
        this.instance = instance;
        this.phero = phero;
        this.alpha = alpha;
        this.beta = beta;
        this.q0 = q0;
        for(int i=0;i<instance.numRows;i++){
            this.uncovRows.add(i);  // U in python
        }
        for(int i=0;i<instance.numCols;i++){
            this.unusedCols.add(i);  // N in python
        }
        for(int i=0;i<instance.numCols;i++){
            int sum = 0;
            for(int j=0;j<instance.numRows;j++){
                sum += instance.A.get(j).get(i);
            }
            this.numRowsCovByCol.add(sum);  // r in python
        }
    }

    void getCost(){
        this.objVal = 0;
        for(int col : this.usedCols){
            this.objVal += this.instance.cost.get(col);
        }
    }
    
    boolean isFeasible(){
        boolean feasible = true;

        for(int j=0;j<this.instance.numRows;j++){
            int sum = 0;
            for(int i : this.usedCols){
                sum += this.instance.A.get(j).get(i);

                if(sum>=1){
                    break;
                }
            }
            if(sum==0){
                feasible = false;
                break;
            }
            if(!feasible){
                break;
            }
        }

        return feasible;
    }

    void constructSolution(){
        while(!this.isFeasible()){
            // For each column calculate number of covered rows divided by the cost of the column
            ArrayList<Double> weights = new ArrayList<Double>();
            for(int i=0;i<this.instance.numCols;i++){
                double greedyVal = (double) this.numRowsCovByCol.get(i) / this.instance.cost.get(i);
                weights.add(Math.pow(phero[i], this.alpha) * Math.pow(greedyVal, this.beta));
            }

            // Choose col with weigted prob
            Random rand = new Random();
            double randDouble = rand.nextDouble();
            int col;
            if(randDouble>this.q0){
                col = weightedRandom(weights);
            } else {
                col = argmax(weights);
            }
            ArrayList<Integer> coveredByCol = new ArrayList<Integer>();

            // Find rows covered by the chosen column
            for(int j : uncovRows){
                if(this.instance.A.get(j).get(col) == 1){
                    coveredByCol.add(j);
                    continue;
                }
            }

            // Remove the rows covered by chosen column
            for(int row : coveredByCol){
                uncovRows.remove(Integer.valueOf(row));
            }

            // Update how many rows each column covers to account for already covered rows
            for(int i : unusedCols){
                int oldVal = this.numRowsCovByCol.get(i);
                int changeIdxi = 0;
                for(int j : coveredByCol){
                    changeIdxi += this.instance.A.get(j).get(i);
                }
                this.numRowsCovByCol.set(i, oldVal - changeIdxi);
            }

            // Add chosen col to solution
            this.usedCols.add(col);
        }
    }

    void localSearch(int steps){
        // Step 1: Remove redundant columns
        int[] costOfUsed = new int[this.instance.cost.size()];

        for(int i : this.usedCols){
            costOfUsed[i] = this.instance.cost.get(i);
        }
        while(sumArray(costOfUsed)!=0){
            int idxMax = argmax(costOfUsed);
            costOfUsed[idxMax] = 0;

            this.usedCols.remove(Integer.valueOf(idxMax));
            if(!this.isFeasible()){
                this.usedCols.add(idxMax);
            }

        }

        if(steps==1){
            return;
        }

        // Step 2: Exchanging columns
        boolean improvement = true;
        while(improvement){
            improvement = false;

            ArrayList<Integer> copyUsedCols = new ArrayList<Integer>(this.usedCols);
            ArrayList<Integer> copyUnusedCols = new ArrayList<Integer>(this.unusedCols);

            for(int colIn : copyUsedCols){
                for(int colOut : copyUnusedCols){
                    if(this.instance.cost.get(colIn) > this.instance.cost.get(colOut)){
                        this.usedCols.add(colOut);
                        this.usedCols.remove(Integer.valueOf(colIn));
                        if(this.isFeasible()){
                            this.unusedCols.remove(Integer.valueOf(colOut));
                            this.unusedCols.add(colIn);
                            improvement = true;
                        } else {
                            this.usedCols.add(colIn);
                            this.usedCols.remove(Integer.valueOf(colOut));
                        }
                        if(improvement){
                            break;
                        }
                    }
                }
                if(improvement){
                    break;
                }
            }
        }

        if(steps==2){
            return;
        }

        // Step 3: Exchanging 2-to-1
        improvement = true;
        while(improvement){
            improvement = false;

            ArrayList<Integer> copyUsedCols = new ArrayList<Integer>(this.usedCols);
            ArrayList<Integer> copyUnusedCols = new ArrayList<Integer>(this.unusedCols);

            for(int colIn1 : copyUsedCols){
                for(int colIn2 : copyUsedCols){
                    if(colIn1 == colIn2){
                        continue;
                    }
                    for(int colOut : copyUnusedCols){
                        if(this.instance.cost.get(colOut) < this.instance.cost.get(colIn1) + this.instance.cost.get(colIn2)){
                            this.usedCols.add(colOut);
                            this.usedCols.remove(Integer.valueOf(colIn1));
                            this.usedCols.remove(Integer.valueOf(colIn2));

                            if(this.isFeasible()){
                                this.unusedCols.remove(Integer.valueOf(colOut));
                                this.unusedCols.add(colIn1);
                                this.unusedCols.add(colIn2);
                                improvement = true;
                            } else {
                                this.usedCols.add(colIn1);
                                this.usedCols.add(colIn2);
                                this.usedCols.remove(Integer.valueOf(colOut));
                            }
                            if(improvement){
                                break;
                            }
                        }
                    }
                    if(improvement){
                        break;
                    }
                }
                if(improvement){
                    break;
                }
            }
        }
    }

    double sumArray(ArrayList<Double> array){
        double sum = 0;
        for(int i=0;i<array.size();i++){
            sum += array.get(i);
        }
        return sum;
    }
    int sumArray(int[] array){
        int sum = 0;
        for(int i=0;i<array.length;i++){
            sum += array[i];
        }
        return sum;
    }

    int weightedRandom(ArrayList<Double> weights){
        ArrayList<Double> probs = new ArrayList<Double>();
        ArrayList<Double> cumProbs = new ArrayList<Double>();

        double sumWeights = sumArray(weights);
        for(int i=0;i<weights.size();i++){
            probs.add(weights.get(i) / sumWeights*100);
            cumProbs.add(sumArray(probs));
        }
        Random rand = new Random();
        double randDouble = rand.nextDouble()*100;

        for(int i=0;i<weights.size();i++){
            if(randDouble < cumProbs.get(i)){
                return i;
            }
        }

        return weights.size();
    }

    public int argmax(int[] array){
        int idxMaxVal = 0;
        int maxVal = array[idxMaxVal];

        for(int i=1;i<array.length;i++){
            if(array[i] > maxVal){
                idxMaxVal = i;
                maxVal = array[idxMaxVal];
            }
        }
        
        return idxMaxVal;
    }

    public int argmax(ArrayList<Double> array){
        int idxMaxVal = 0;
        double maxVal = array.get(idxMaxVal);

        for(int i=1;i<array.size();i++){
            if(array.get(i) > maxVal){
                idxMaxVal = i;
                maxVal = array.get(idxMaxVal);
            }
        }
        
        return idxMaxVal;
    }

    public int argmin(int[] array){
        int idxMinVal = 0;
        int minVal = array[idxMinVal];

        for(int i=1;i<array.length;i++){
            if(array[i] < minVal){
                idxMinVal = i;
                minVal = array[idxMinVal];
            }
        }
        
        return idxMinVal;
    }

    public int argmin(ArrayList<Integer> array){
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

}
