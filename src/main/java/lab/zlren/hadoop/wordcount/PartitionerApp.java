package lab.zlren.hadoop.wordcount;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * 使用MapReduce开发WordCount应用
 *
 * @author zlren
 * @date 2017-11-17
 */
@Slf4j
public class PartitionerApp {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        LongWritable one = new LongWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] words = line.split(" ");

            context.write(new Text(words[0]), new LongWritable(Long.valueOf(words[1])));
        }
    }

    /**
     * 归并操作
     */
    public static class MyReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        /**
         * @param key
         * @param values  shuffle之后，这里是个集合
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
            long sum = 0;
            for (LongWritable value : values) {
                // 将次数累加
                sum += value.get();
            }

            // 结果交给上下文
            context.write(key, new LongWritable(sum));
        }
    }


    public static class MyPartitioner extends Partitioner<Text, LongWritable> {

        @Override
        public int getPartition(Text key, LongWritable value, int numPartitions) {

            if ("xiaomi".equals(key.toString())) {
                return 0;
            }

            if ("huawei".equals(key.toString())) {
                return 1;
            }

            if ("iphone7".equals(key.toString())) {
                return 2;
            }

            return 3;
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {

        Configuration configuration = new Configuration();
        Job job = Job.getInstance(configuration, "wordcount");

        // 准备清理已存在的输出目录
        Path outPutPath = new Path(args[1]);
        FileSystem fileSystem = FileSystem.get(configuration);
        if (fileSystem.exists(outPutPath)) {
            fileSystem.delete(outPutPath, true);
            log.info("输出目录已经删除");
        }

        job.setJarByClass(PartitionerApp.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // partitioner
        job.setPartitionerClass(MyPartitioner.class);
        // 为什么是4？在partition中分了4类
        job.setNumReduceTasks(4);

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

