#!/bin/env bash
dir=/home/lxu/ICDM/UCR_TS_Archive_2015
rm -rf ./run/
rm -rf ./out/
mkdir ./run/
mkdir ./out/
module load java/1.8.0_77
for dataset in `ls $dir`;do
	echo "creating $dataset.sh"
	echo "#!/bin/sh" >> ./run/run-$dataset.sh
	echo "#SBATCH --job-name=time_sequences_classify" >> ./run/run-$dataset.sh
	echo "#SBATCH --mem=4000" >> ./run/run-$dataset.sh
	echo "#SBATCH --output=./out/$dataset.out" >> ./run/run-$dataset.sh
	echo "module load java/1.8.0_77" >> ./run/run-$dataset.sh
	echo "java -Xmx2g -Dfile.encoding=UTF-8 -classpath /home/lxu/ICDM/bin:/home/lxu/ICDM/lib/weka.jar:/home/lxu/ICDM/lib/commons-math3-3.2.jar classif.ExperimentsLauncher $dataset">> ./run/run-$dataset.sh
chmod +x ./run/run-$dataset.sh
done

for dataset in `ls $dir`;do
	echo "launching run-$dataset.sh"
	sbatch /home/lxu/ICDM/run/run-$dataset.sh
done

