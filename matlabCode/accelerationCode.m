filename = '/home/hauser2016/Project/sensorgrabber/pythonCode/data_with_plots/4-15-2015/05-25-2015 13:37:10.csv';
M = csvread(filename);
time = M(:,1);
X = M(:,6);
Y = M(:,7);
Z = M(:,8);

Data = sqrt((X.^2)+(Y.^2)+(Z.^2));

sData = smooth(Data,0.2,'loess');

figure (1);
plot(time, sData, '-o');

figure (2);
plot(time,Data,'-*');
