filename = '/home/hauser2016/Project/sensorgrabber/pythonCode/data_with_plots/4-15-2015/05-25-2015 13:37:10.csv';
M = csvread(filename);
time = M(:,1);
azimuth = M(:,2);
pitch = M(:,3);
roll = M(:,4);

%converting from rad to deg
azi = rad2deg(azimuth);
%pit = rad2deg(pitch);
%rol = rad2deg(roll);

%smoothing
azis = smooth(azi,0.3,'rloess');
%pits = smooth(pit,0.3,'rloess');
%rols = smooth(rol,0.3,'rloess');

%unfiltered plot
figure(1);
plot(time,azi,'-O');
axis([0 inf -180 180]); 

hold on

%smooth plot
figure(2);
plot(time,azis,'-O');
axis([0 inf -180 180]);

