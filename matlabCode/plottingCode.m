filename = '/home/hauser2016/Project/sensorgrabber/pythonCode/data_with_plots/4-15-2015/05-25-2015 13:37:10.csv';
%open file and change date to datenum format
fid = fopen(filename, 'rt');
a = textscan(fid, '%s %f %f %f %f %f %f', 'Delimiter', ',','CollectOutput',1);
fclose(fid);

%dts is number of seconds in a day
%filename is first date and time
dts = 24*3600;
k = max(strfind(filename,'/'));
datetime = filename(k+1:(end-4));

formatIn = 'mm-dd-yyyy HH:MM:SS';
d1 = datenum(datetime, formatIn)*dts;
formatIn = 'mm/dd/yyyy HH:MM:SS';
time = ((datenum(M(:,1)))*dts)-d1;
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

