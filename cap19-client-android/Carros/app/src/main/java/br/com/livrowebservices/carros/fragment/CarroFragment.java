package br.com.livrowebservices.carros.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URL;

import br.com.livrowebservices.carros.R;
import br.com.livrowebservices.carros.activity.CarroActivity;
import br.com.livrowebservices.carros.domain.Carro;
import br.com.livrowebservices.carros.domain.CarroService;
import br.com.livrowebservices.carros.fragment.dialog.DeletarCarroDialog;
import livroandroid.lib.fragment.BaseFragment;
import livroandroid.lib.utils.IntentUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarroFragment extends BaseFragment {

    private TextView tNome;
    private TextView tDesc;
    private TextView tLat;
    private TextView tLng;
    private ImageView img;
    private Carro c;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carro, container, false);
        setHasOptionsMenu(true);

        img = (ImageView) view.findViewById(R.id.img);
        tNome = (TextView) view.findViewById(R.id.tNome);
        tDesc = (TextView) view.findViewById(R.id.tDesc);
        tLat = (TextView) view.findViewById(R.id.tLat);
        tLng = (TextView) view.findViewById(R.id.tLng);

        if(getArguments() != null) {
            c = (Carro) getArguments().getSerializable("carro");
            setCarro(c);
        }
        return view;
    }

    private void setCarro(Carro c) {
        if (c != null) {
            if(c.urlFoto != null && c.urlFoto.trim().length() > 0) {
                Picasso.with(getContext()).load(c.urlFoto).placeholder(R.drawable.placeholder).into(img);
            } else {
                img.setImageResource(R.drawable.placeholder);
            }
        }

        for (int i=0;i<10;i++){
            c.desc += "\n"+c.desc;
        }

        tNome.setText(c.nome);
        tDesc.setText(c.desc);
        tLat.setText(c.latitude);
        tLng.setText(c.longitude);

        CarroActivity activity = (CarroActivity) getActivity();
        activity.setAppBarHeaderImage(c.urlFoto);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_carro, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            toast("Editar: " + c.nome);
            return true;
        } else if (id == R.id.action_remove) {
            DeletarCarroDialog.show(getFragmentManager(), new DeletarCarroDialog.Callback() {
                @Override
                public void deleteCarro() {
                    startTask("deletarCarro",taskDeleteCarro());
                }
            });

            return true;
        } else if (id == R.id.action_video) {
            // Abre o vídeo no Player de Vídeo Nativo
            if(c.urlVideo != null && c.urlVideo.trim().length() > 0) {
                if(URLUtil.isValidUrl(c.urlVideo)) {
                    IntentUtils.showVideo(getContext(), c.urlVideo);
                } else {
                    toast(getString(R.string.msg_url_invalida));
                }

            } else {
                toast(getString(R.string.msg_carro_sem_video));
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BaseTask taskDeleteCarro() {
        return new BaseTask(){
            @Override
            public Object execute() throws Exception {
                CarroService.delete(getContext(),c);
                return null;
            }

            @Override
            public void updateView(Object response) {
                super.updateView(response);
                toast("Carro " + c.nome + " excluído com sucesso");
                getActivity().finish();
            }
        };
    }

}
