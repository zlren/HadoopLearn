package lab.zlren.hadoop.wordcount;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
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
public class WordCountWithCombinerApp {

    public static class MyMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        LongWritable one = new LongWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();
            String[] words = line.split(" ");
            for (String word : words) {
                // 通过context将map作业的结果输出
                context.write(new Text(word), one);
            }
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

        job.setJarByClass(WordCountWithCombinerApp.class);

        FileInputFormat.setInputPaths(job, new Path(args[0]));

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(MyReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        // 设置Combiner
        job.setCombinerClass(MyReducer.class);

        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);

        log.info("哈哈");
    }
}

